package app.provider.bestpricedelivery.Bookings;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class NewBooking extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;
    LinearLayout orderListLayout;
    TextView timerText;
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_booking);
        this.setFinishOnTouchOutside(false);
        timerText = findViewById(R.id.timerText);
        mActivity = this;

        userType = Constants.getUserType(mActivity);
        initialize();
        getBookingDetails();
    }

    ImageView customerImage, sendSMSIV, mobIV, locIv;
    TextView acceptBooking, rejectBooking, userDistance, customerName, customerAddress, customerContactNumber;

    void initialize() {
        try {
            customerImage = findViewById(R.id.customerImage);
            orderListLayout = findViewById(R.id.orderListLayout);
            userDistance = findViewById(R.id.userDistance);
            customerName = findViewById(R.id.customerName);
            customerAddress = findViewById(R.id.customerAddress);
            customerContactNumber = findViewById(R.id.customerContactNumber);
            locIv = findViewById(R.id.locIv);
            sendSMSIV = findViewById(R.id.sendSMSIV);
            mobIV = findViewById(R.id.mobIV);
            rejectBooking = findViewById(R.id.rejectBooking);
            rejectBooking.setOnClickListener(this);
            acceptBooking = findViewById(R.id.acceptBooking);
            acceptBooking.setOnClickListener(this);
            sendSMSIV.setOnClickListener(this);
            mobIV.setOnClickListener(this);
            locIv.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void getBookingDetails() {
        try {
            String bookingID = getIntent().getStringExtra("booking_id");
            String otp = new GlobalAppApis().sendBookingDetails(this, bookingID);
            ApiService client = getApiClient_ByPost();
            Call<String> call;
            if (!userType.equalsIgnoreCase("Stall")) {
                call = client.getOrderDetails(Constants.getAPIToken(mActivity), otp);
            } else {
                call = client.getStallDetails(Constants.getAPIToken(mActivity), otp);
            }
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void acceptRejectOrder(String status, String msg) {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();

            }
            Call<String> call;
            ApiService client = getApiClient_ByPost();
            if (!userType.equalsIgnoreCase("stall")) {
                String bookingID = getIntent().getStringExtra("booking_id");
                if (status.equalsIgnoreCase("accepted")) {
                    String otp = new GlobalAppApis().sendAcceptDetails(this, bookingID);
                    call = client.acceptOrder(Constants.getAPIToken(mActivity), otp);
                } else {
                    String otp = new GlobalAppApis().sendCancelledDetails(this, bookingID,msg);
                    call = client.cancelOrder(Constants.getAPIToken(mActivity), otp);
                }

            } else {
                String bookingID = getIntent().getStringExtra("booking_id");
                String otp = new GlobalAppApis().sendAcceptRejectDetails(this, bookingID, status, msg);
                call = client.acceptrejectOrder(Constants.getAPIToken(mActivity), otp);

            }
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.has("data")) {
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

    void setData(JSONObject data) {
        try {
            if (data.has("items") && data.getJSONArray("items").length() > 0) {
                JSONArray orderDetailArray = data.getJSONArray("items");
                addOrderRows(orderDetailArray);
                setCustomerDetails(data);
                setDistance(data);
                setCustomerAddress(data.getString("shippingAddress"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String customerNumber = "";

    void setCustomerDetails(JSONObject customerJSON) {
        try {
            Glide.with(mActivity).load(customerJSON.getString("customer_image"))
                    .apply(new RequestOptions().error(R.drawable.app_logo).placeholder(R.drawable.app_logo))
                    .into(customerImage);
            customerName.setText(customerJSON.getString("customer_name"));
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
//            String userAddress = (resultJSON.has("street") ? resultJSON.getString("street") + "," : "") + " "
//                    + (resultJSON.has("city") ? resultJSON.getString("city") + "," : "") + " "
//                    + (resultJSON.has("state") ? resultJSON.getString("state") + "," : "") + " "
//                    + (resultJSON.has("country") ? resultJSON.getString("country") + "," : "") + " "
//                    + (resultJSON.has("pin_code") ? resultJSON.getString("pin_code") : "");
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
            case R.id.acceptBooking:
                acceptRejectOrder("accepted", "");
                break;
            case R.id.rejectBooking:
                rejectOrder();


                //                acceptRejectOrder("rejected");
                break;
        }
    }

    Dialog mDialog;

    void rejectOrder() {
        mDialog = new CancelOrderDialog(mActivity, new CancelOrderDialog.ConfirmationDialogListener() {
            @Override
            public void onYesButtonClicked(CancelOrderDialog.confirmTask task, String msg) {
//                cancelOrder(msg);
                acceptRejectOrder("rejected", msg);
            }

            @Override
            public void onNoButtonClicked(CancelOrderDialog.confirmTask task) {

            }
        }, CancelOrderDialog.confirmTask.NONE);
    }
}
