package app.provider.bestpricedelivery.Tender;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Constants.CircleTransform;
import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class BookingDetailsForTender extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;
    LinearLayout orderListLayout;
    String order_id = "";
    String stall_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details_tender);
        mActivity = this;
        initialize();
        order_id = getIntent().getStringExtra("order_id");
        try {
            getBookingListing(order_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ImageView customerImage, sendSMSIV, mobIV, locIv;
    TextView acceptBooking, rejectBooking, userDistance, customerName, customerAddress, customerContactNumber;
    TextView totalBill, headerTxt;
    ImageView proceedToDeliveryBtn;
    TextView assignedWorkerName;
    ImageView backImg;

    void initialize() {
        try {
            headerTxt = findViewById(R.id.headerTxt);
            backImg = findViewById(R.id.backImg);
            proceedToDeliveryBtn = findViewById(R.id.proceedToDeliveryBtn);
            assignedWorkerName = findViewById(R.id.assignedWorkerName);
            customerImage = findViewById(R.id.customerImage);
            orderListLayout = findViewById(R.id.orderListLayout);
            userDistance = findViewById(R.id.userDistance);
            customerName = findViewById(R.id.customerName);
            customerAddress = findViewById(R.id.customerAddress);
            customerContactNumber = findViewById(R.id.customerContactNumber);
            locIv = findViewById(R.id.locIv);
            sendSMSIV = findViewById(R.id.sendSMSIV);
            totalBill = findViewById(R.id.totalBill);
            mobIV = findViewById(R.id.mobIV);
            headerTxt.setText("Booking Detail");
            proceedToDeliveryBtn.setOnClickListener(this);
            sendSMSIV.setOnClickListener(this);
            mobIV.setOnClickListener(this);
            locIv.setOnClickListener(this);
            backImg.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBookingListing(String order_id) {
        String otp = new GlobalAppApis().getBookingDetailsForTender(order_id);
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getOrderDetailForRestaurant(Constants.getAPIToken(mActivity), otp);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {
                    if (jsonObject.has("data") && jsonObject.getJSONObject("data").length() > 0) {

                        setData(jsonObject.getJSONObject("data"));
                    } else {

                    }
                } else {
                }
            }

        }, mActivity, call, "", true);

    }


    void setData(JSONObject data) {
        try {
            if (data.has("items") && data.getJSONArray("items").length() > 0) {
                JSONArray orderDetailArray = data.getJSONArray("items");
                addOrderRows(orderDetailArray);
                setCustomerDetails(data);
                setDistance(data);
                setCustomerAddress(data.getString("shippingAddress"));
                totalBill.setText(getResources().getString(R.string.rupee) + " " + (int) Math.round(Double.parseDouble(data.getString("total"))) + "");
                try {
                    assignedWorkerName.setText(data.getJSONObject("stall").getString("name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String customerNumber = "";
    double totalAmt = 0;

    void setCustomerDetails(JSONObject customerJSON) {
        try {
            Glide.with(mActivity).load(customerJSON.getString("customer_image"))
                    .apply(new RequestOptions().error(R.drawable.app_logo).transform(new CircleTransform(mActivity))
                            .placeholder(R.drawable.app_logo))
                    .into(customerImage);
            customerName.setText(customerJSON.getString("customer_name"));
            assignedWorkerName.setText(customerJSON.getString("deliveryboy_name"));
            customerNumber = customerJSON.getString("mobile");
            customerContactNumber.setText("+91 " + customerNumber);
            customerlat = Double.parseDouble(customerJSON.getString("latitude"));
            customerLong = Double.parseDouble(customerJSON.getString("longitude"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    double customerlat = 0, customerLong = 0;

    void setCustomerAddress(String userAddress) {
        try {
            customerAddress.setText(userAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setDistance(JSONObject customerJSON) {
        try {

            double distance = Constants.getDistanceInKm(Double.parseDouble(customerJSON.getString("latitude").length() == 0 ? "0" : customerJSON.getString("latitude")),
                    Double.parseDouble(customerJSON.getString("longitude").length() == 0 ? "0" : customerJSON.getString("longitude")),
                    Constants.getLatitude(mActivity), Constants.getLongitude(mActivity));
            userDistance.setText(Math.round(distance) + " km away.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addOrderRows(JSONArray orderDetailArray) {
        try {
            orderListLayout.removeAllViews();
            for (int i = 0; i < orderDetailArray.length(); i++) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.order_row, null);
                TextView itemName = view.findViewById(R.id.itemName);
                TextView cosdtTv = view.findViewById(R.id.cosdtTv);
                TextView qntyTv = view.findViewById(R.id.qntyTv);
                try {
                    itemName.setText(orderDetailArray.getJSONObject(i).getString("dish_name"));
                    qntyTv.setText(orderDetailArray.getJSONObject(i).getString("dish_qty") + " pcs");
                    totalAmt = totalAmt + Double.parseDouble(orderDetailArray.getJSONObject(i).getString("dish_qty")) *
                            Double.parseDouble(orderDetailArray.getJSONObject(i).getString("dish_sale_price"));
                    cosdtTv.setText(getResources().getString(R.string.rupee) + " " + Double.parseDouble(orderDetailArray.getJSONObject(i).getString("dish_qty")) *
                            Double.parseDouble(orderDetailArray.getJSONObject(i).getString("dish_sale_price")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                orderListLayout.addView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mobIV:
                Constants.openDiallerPad(customerNumber, mActivity);
                break;
            case R.id.sendSMSIV:
                Constants.sendSMSDialog(customerNumber, mActivity);
                break;
            case R.id.locIv:
                Constants.openGoogleMapNaviagtion(mActivity, customerlat, customerLong);
                break;

            case R.id.proceedToDeliveryBtn:
                proceedToOrder("ontheway");
                break;
            case R.id.backImg:
                finish();
                break;

        }
    }

    void proceedToOrder(String status) {
        try {
            String otp = new GlobalAppApis().sendAcceptRejectDetails_(this, order_id, status);
            ApiService client = getApiClient_ByPost();
            Call<String> call = client.acceptrejectOrder(Constants.getAPIToken(mActivity), otp);
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.has("data") && jsonObject.getJSONObject("data").length() > 0) {
                            Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(mActivity, "Please try again.", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                    }
                }

            }, mActivity, call, "", true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
