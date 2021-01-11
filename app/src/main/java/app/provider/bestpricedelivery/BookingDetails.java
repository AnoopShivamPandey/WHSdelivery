package app.provider.bestpricedelivery;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Bookings.CancelOrderDialog;
import app.provider.bestpricedelivery.Constants.CircleTransform;
import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class BookingDetails extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;
    LinearLayout orderListLayout;
    String order_id = "";
    TextView proceedToDeliveryTv;
    ImageView tickImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        mActivity = this;
        initialize();
        order_id = getIntent().getStringExtra("order_id");
        Log.d("Order_ID", order_id);

    }

    ImageView customerImage, sendSMSIV, mobIV;
    RelativeLayout locIv;
    TextView acceptBooking, rejectBooking, userDistance, customerName, customerAddress, customerContactNumber;
    TextView totalBill, headerTxt;
    ImageView proceedToDeliveryBtn;
    TextView assignedWorkerName, rejectTv;
    ImageView backImg;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            rejectTv.setVisibility(View.GONE);
            order_id = getIntent().getStringExtra("order_id");
            totalAmt = 0;
            getBookingListing(order_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initialize() {
        try {
            rejectTv = findViewById(R.id.rejectTv);
            tickImg = findViewById(R.id.tickImg);
            proceedToDeliveryTv = findViewById(R.id.proceedToDeliveryTv);
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
            rejectTv.setOnClickListener(this);
            proceedToDeliveryTv.setOnClickListener(this);

            tickImg.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBookingListing(String order_id) {
        String otp = new GlobalAppApis().getBookingDetails(order_id, mActivity);
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getOrderDetail(Constants.getAPIToken(mActivity), otp);
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

    String bookingId = "";
    String qrCode = "";

    void setData(JSONObject data) {
        try {
            if (data.has("restaurant")) {
                qrCode = data.getJSONObject("restaurant").getString("qrcode");
                if (qrCode!=null && !qrCode.equalsIgnoreCase("")){
                    tickImg.setVisibility(View.VISIBLE);
                }
            }
            Glide.with(mActivity).load(R.drawable.qrcode).into(tickImg);
            if (data.has("items") && data.getJSONArray("items").length() > 0) {
                JSONArray orderDetailArray = data.getJSONArray("items");
                addOrderRows(orderDetailArray);
                setCustomerDetails(data);
                setDistance(data);
                setCustomerAddress(data.getString("shippingAddress"));
                totalBill.setText(getResources().getString(R.string.rupee) + " " + (int) Math.round(Double.parseDouble(data.getString("total"))) + "");
                if (data.has("status")) {
                    switch (data.getString("status").toLowerCase()) {
//                        case "processing":
//                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.rounded_green));
//                            proceedToDeliveryTv.setText("Start");
//                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
//                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
//                            break;
                        case "accepted":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_colorprimary));
                            proceedToDeliveryTv.setText("Start");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
                            break;
                        case "rejected":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.rounded_red_));
                            proceedToDeliveryTv.setText("Rejected");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
                            break;
                        case "cancelled":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.rounded_red_));
                            proceedToDeliveryTv.setText("cancelled");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
                            break;
                        case "processing":
                        case "out_for_delivery":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_colorprimary));
                            proceedToDeliveryTv.setText("End");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
                            break;

                        case "pending":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_colorprimary));
                            proceedToDeliveryTv.setText("Accept");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
                            rejectTv.setVisibility(View.VISIBLE);
                            break;
                        case "delivered":
                            proceedToDeliveryTv.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.round_colorprimary));
                            proceedToDeliveryTv.setText("Completed");
                            proceedToDeliveryTv.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                            proceedToDeliveryTv.setGravity(Gravity.CENTER);
//                            rejectTv.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                proceedToDeliveryTv.setOnClickListener(this);
                rejectTv.setOnClickListener(this);
                rejectTv.setPadding(40, 15, 40, 15);
                rejectTv.setGravity(Gravity.CENTER);
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
            customerNumber = customerJSON.getString("customer_mobile");
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

            case R.id.tickImg:
                if (qrCode.length() > 0)
                    startActivity(new Intent(mActivity, QRActivity.class)
                            .putExtra("qrcode", qrCode));
                else
                    Toast.makeText(mActivity, "Please try again later", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rejectTv:
                rejectOrder();
                break;
            case R.id.proceedToDeliveryTv:
                String res = proceedToDeliveryTv.getText().toString().trim();
                if (res.equalsIgnoreCase("Accept")) {
                    acceptRejectOrder("accepted");
                } else if (res.equalsIgnoreCase("end")) {
                    acceptRejectOrder("delivered");
                } else if (res.equalsIgnoreCase("start")) {
                    acceptRejectOrder("out_for_delivery");
                    Constants.openGoogleMapNaviagtion(mActivity, customerlat, customerLong);

                }
                break;

        }
    }

    Dialog mDialog;

    void rejectOrder() {
        mDialog = new CancelOrderDialog(mActivity, new CancelOrderDialog.ConfirmationDialogListener() {
            @Override
            public void onYesButtonClicked(CancelOrderDialog.confirmTask task, String msg) {
//                cancelOrder(msg);
                acceptRejectOrder("rejected");
            }

            @Override
            public void onNoButtonClicked(CancelOrderDialog.confirmTask task) {

            }
        }, CancelOrderDialog.confirmTask.NONE);
    }

    void acceptRejectOrder(String status) {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            String bookingID = getIntent().getStringExtra("order_id");
            String otp = new GlobalAppApis().sendAcceptRejectDetails_(this, bookingID, status);
            ApiService client = getApiClient_ByPost();
            Call<String> call = client.changeOrderStatus(Constants.getAPIToken(mActivity), otp);
            new ConnectToRetrofit(new RetrofitCallBackListenar() {
                @Override
                public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.has("data")) {
                            Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                            finish();
                            onResume();
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
