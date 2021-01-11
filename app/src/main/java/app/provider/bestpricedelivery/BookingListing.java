package app.provider.bestpricedelivery;

import   android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.provider.bestpricedelivery.Constants.CircleTransform;
import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class BookingListing extends AppCompatActivity {
    RecyclerView bookingListRecyclerView;
    Activity mActivity;
    ImageView backImg;
    TextView headerTxt, noRecordsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_listing);
        mActivity = this;
        bookingListRecyclerView = findViewById(R.id.bookingListRecyclerView);
        backImg = findViewById(R.id.backImg);
        headerTxt = findViewById(R.id.headerTxt);
        noRecordsText = findViewById(R.id.noRecordsText);
        headerTxt.setText("Booking List");
        bookingListRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bookingListRecyclerView.setLayoutManager(layoutManager);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getBookingListing();

    }

    public void getBookingListing() {
        String otp = new GlobalAppApis().sendBookingList(this);
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getStallListing(Constants.getAPIToken(mActivity), otp);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {
                    if (jsonObject.has("data") && jsonObject.getJSONArray("data").length() > 0) {
                        showNoRecords(false);
                        setData(jsonObject);
                    } else {

                        showNoRecords(true);
                    }
                } else {
                    showNoRecords(true);
                }
            }

        }, mActivity, call, "", true);


    }

    private void showNoRecords(boolean show) {
        if (show) {
            noRecordsText.setVisibility(View.VISIBLE);
            bookingListRecyclerView.setVisibility(View.GONE);
        } else {
            noRecordsText.setVisibility(View.GONE);
            bookingListRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    void setData(JSONObject data) {
        try {
            ArrayList<JSONObject> resultList = new ArrayList<>();
            for (int i = 0; i < data.getJSONArray("data").length(); i++) {
                resultList.add(data.getJSONArray("data").getJSONObject(i));
            }
            if (resultList.size() > 0) {
                bookingListRecyclerView.setAdapter(new BookingsAdapter(resultList));
            } else {
                showNoRecords(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingsHolder> {
        ArrayList<JSONObject> resultList;

        public BookingsAdapter(ArrayList<JSONObject> resultList) {
            this.resultList = resultList;
        }

        @NonNull
        @Override
        public BookingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mView = LayoutInflater.from(mActivity).inflate(R.layout.include_booking_list_row, null);
            return new BookingsHolder(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingsHolder holder, int position) {
            try {
                final JSONObject resultJSON = resultList.get(position);
                holder.customerName.setText(resultJSON.has("customer_name") ?
                        resultJSON.getString("customer_name") : "N/A");
                holder.customerContactNumber.setText(resultJSON.has("customer_mobile") ?
                        resultJSON.getString("customer_mobile") : "N/A");
                if (resultJSON.has("customer_image") && resultJSON.getString("customer_image").length() > 0) {
                    Glide.with(mActivity)
                            .load(resultJSON.getString("customer_image"))
                            .apply(new RequestOptions().transform(new CircleTransform(mActivity))
                                    .error(R.drawable.app_logo).placeholder(R.drawable.app_logo))
                            .into(holder.customerImage);
                }
                try {
                    holder.bookingStatus.setText(resultJSON.getString("orderstatus"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultJSON.has("customer_mobile") && resultJSON.getString("customer_mobile").length() > 0) {
                    holder.mobIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Constants.openDiallerPad(resultJSON.getString("customer_mobile"), mActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.smsIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Constants.sendSMSDialog(resultJSON.getString("customer_mobile"), mActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    holder.mobIV.setVisibility(View.GONE);
                    holder.smsIv.setVisibility(View.GONE);
                }
                try {
                    holder.customerDistance.setText(setDistance(resultJSON));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String userAddress = (resultJSON.has("shippingAddress") ? resultJSON.getString("shippingAddress") + "," : "") + " ";
                    holder.customerAddress.setText(userAddress);
                } catch (Exception e) {
                    holder.customerAddress.setText("N/A");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }

        String setDistance(JSONObject customerJSON) {
            try {

                double distance = Constants.getDistanceInKm(Double.parseDouble(customerJSON.getString("latitude").length() == 0 ? "0" :
                                customerJSON.getString("longitude")),
                        Double.parseDouble(customerJSON.getString("latitude").length() == 0 ? "0" : customerJSON.getString("longitude")),
                        Constants.getLatitude(mActivity), Constants.getLongitude(mActivity));
                return (Math.round(distance) + " km.");
            } catch (Exception e) {
                e.printStackTrace();
                return "0 km";
            }
        }

        class BookingsHolder extends RecyclerView.ViewHolder {
            ImageView customerImage, mobIV, smsIv;
            TextView customerName;
            TextView customerAddress;
            TextView customerContactNumber;
            TextView customerDistance;
            TextView bookingStatus;

            public BookingsHolder(@NonNull View itemView) {
                super(itemView);
                mobIV = itemView.findViewById(R.id.mobIV);
                bookingStatus = itemView.findViewById(R.id.bookingStatus);
                smsIv = itemView.findViewById(R.id.smsIv);
                customerImage = itemView.findViewById(R.id.customerImage);
                customerName = itemView.findViewById(R.id.customerName);
                customerAddress = itemView.findViewById(R.id.customerAddress);
                customerContactNumber = itemView.findViewById(R.id.customerContactNumber);
                customerDistance = itemView.findViewById(R.id.customerDistance);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startActivity(new Intent(mActivity, BookingDetails.class)
                                    .putExtra("order_id", resultList.get(getAdapterPosition()).getString("id")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    }
}