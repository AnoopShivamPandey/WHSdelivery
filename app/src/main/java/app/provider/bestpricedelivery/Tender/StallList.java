package app.provider.bestpricedelivery.Tender;

import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class StallList extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_list);
        mActivity = this;
        initialize();
        getStallList();
    }

    private void showNoRecords(boolean show) {
        if (show) {
            noRecordsText.setVisibility(View.VISIBLE);
            stallListingRv.setVisibility(View.GONE);
        } else {
            noRecordsText.setVisibility(View.GONE);
            stallListingRv.setVisibility(View.VISIBLE);
        }
    }

    TextView headerTxt, noRecordsText;
    ImageView backImg;
    RecyclerView stallListingRv;

    void initialize() {
        headerTxt = findViewById(R.id.headerTxt);
        backImg = findViewById(R.id.backImg);
        stallListingRv = findViewById(R.id.stallListingRv);
        noRecordsText = findViewById(R.id.noRecordsText);
        headerTxt.setText("Stall List");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        stallListingRv.setLayoutManager(linearLayoutManager);
        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
        }
    }

    void getStallList() {
        try {
            String otp = new GlobalAppApis().getStallList(this);
            ApiService client = getApiClient_ByPost();
            Call<String> call = client.getStallList(Constants.getAPIToken(mActivity), otp);
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        if (jsonObject.getJSONArray("data").length() == 0) {
                            showNoRecords(true);
                        } else {
                            setData(jsonObject);
                            showNoRecords(false);
                        }
                    } else {
                        Toast.makeText(mActivity, "Please try again later.", Toast.LENGTH_SHORT).show();
                        showNoRecords(true);
                    }
                }

            }, mActivity, call, "", true);


        } catch (Exception e) {
            e.printStackTrace();
            showNoRecords(true);
        }
    }

    void setData(JSONObject resultJSON) {
        try {
            ArrayList<JSONObject> resultList = new ArrayList<>();
            for (int i = 0; i < resultJSON.getJSONArray("data").length(); i++) {
                resultList.add(resultJSON.getJSONArray("data").getJSONObject(i));
            }
            if (resultList.size() > 0) {
                BookingsAdapter bookingsAdapter = new BookingsAdapter(resultList);
                stallListingRv.setAdapter(bookingsAdapter);
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
        public BookingsAdapter.BookingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mView = LayoutInflater.from(mActivity).inflate(R.layout.include_stall_list_row, null);
            return new BookingsAdapter.BookingsHolder(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingsAdapter.BookingsHolder holder, int position) {
            try {
                final JSONObject resultJSON = resultList.get(position).getJSONObject("stall");
                holder.customerName.setText(resultJSON.has("name") ?
                        resultJSON.getString("name") : "N/A");
                holder.customerContactNumber.setText(resultJSON.has("mobile") ?
                       "+91 "+ resultJSON.getString("mobile") : "N/A");
                if (resultJSON.has("profile_picture") && resultJSON.getString("profile_picture").length() > 0) {
                    Glide.with(mActivity)
                            .load(resultJSON.getString("profile_picture"))
                            .apply(new RequestOptions().error(R.drawable.app_logo).placeholder(R.drawable.app_logo))
                            .into(holder.customerImage);
                }

                if (resultJSON.has("mobile") && resultJSON.getString("mobile").length() > 0) {
                    holder.mobIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(Intent.ACTION_DIAL);
//                            try {
//                                intent.setData(Uri.parse("tel:" + resultJSON.getString("customer_phone")));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            startActivity(intent);
                            try {
                                Constants.openDiallerPad(resultJSON.getString("mobile"), mActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.smsIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Constants.sendSMSDialog(resultJSON.getString("mobile"), mActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*String uri = null;
                            try {
                                uri = "smsto:" + resultJSON.getString("customer_phone");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                            intent.putExtra("sms_body", "");
                            intent.putExtra("compose_mode", true);
                            startActivity(intent);*/
                        }
                    });
                } else {
                    holder.mobIV.setVisibility(View.GONE);
                    holder.smsIv.setVisibility(View.GONE);
                }

                try {
//                    String userAddress = (resultJSON.has("street") ? resultJSON.getString("street") + "," : "") + " "
//                            + (resultJSON.has("city") ? resultJSON.getString("city") + "," : "") + " "
//                            + (resultJSON.has("state") ? resultJSON.getString("state") + "," : "") + " "
//                            + (resultJSON.has("country") ? resultJSON.getString("country") + "," : "") + " "
//                            + (resultJSON.has("pin_code") ? resultJSON.getString("pin_code") : "");
//                    holder.customerAddress.setText(userAddress);
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
//            return 10;
        }

        class BookingsHolder extends RecyclerView.ViewHolder {
            ImageView customerImage, mobIV, smsIv;
            TextView customerName;
            TextView customerAddress;
            TextView customerContactNumber;
            TextView customerDistance;

            public BookingsHolder(@NonNull View itemView) {
                super(itemView);
                mobIV = itemView.findViewById(R.id.mobIV);
                smsIv = itemView.findViewById(R.id.smsIv);
                customerImage = itemView.findViewById(R.id.customerImage);
                customerName = itemView.findViewById(R.id.customerName);
                customerAddress = itemView.findViewById(R.id.customerAddress);
                customerContactNumber = itemView.findViewById(R.id.customerContactNumber);
                customerDistance = itemView.findViewById(R.id.customerDistance);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity,ProfileDetailsForStall.class)
                .putExtra("stallDetails",resultList.get(getAdapterPosition()).toString()));
            }
        });
            }
        }
    }

}
