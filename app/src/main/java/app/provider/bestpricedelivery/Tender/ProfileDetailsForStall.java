package app.provider.bestpricedelivery.Tender;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.Services.StallInventory;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class ProfileDetailsForStall extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_stall);
        mActivity = this;
        initialize();
        updateDetails();
    }

    String stall_id = "";

    void updateDetails() {
        try {
            JSONObject json = new JSONObject(getIntent().getStringExtra("stallDetails"));
            JSONObject resultJSON = json
                    .getJSONObject("stall");
            if (resultJSON.has("profile_picture") && resultJSON.getString("profile_picture").length() > 0) {
                Glide.with(mActivity)
                        .load(resultJSON.getString("profile_picture"))
                        .apply(new RequestOptions().error(R.drawable.app_logo).placeholder(R.drawable.app_logo))
                        .into(stallImage);
            }
            stall_id = json.getString("id");
            userName.setText(resultJSON.has("name") ?
                    resultJSON.getString("name") : "N/A");
            userMobileNumber.setText(resultJSON.has("mobile") ?
                    "+91 " + resultJSON.getString("mobile") : "N/A");
            totalBookings.setText(resultJSON.getString("total_order"));
            todayBookings.setText(resultJSON.getString("todays_order"));
            totalEarnings.setText(getResources().getString(R.string.rupee) + resultJSON.getString("total_earning"));
            todayEarning.setText(getResources().getString(R.string.rupee) + resultJSON.getString("todays_earning"));
            if ((resultJSON.getString("profile_status").equalsIgnoreCase("online"))) {
                statusBtn.setText("ONLINE");
                statusBtn.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.rounded_green));
                statusBtn.setPadding(40, 15, 40, 15);
            } else {
                statusBtn.setText("OFFLINE");
                statusBtn.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.rounded_red));
                statusBtn.setPadding(40, 15, 40, 15);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    EditText userName, userMobileNumber, userAddress1, userAddress2;
    ImageView backImg, stallImage;
    TextView headerTxt;
    TextView statusBtn, totalEarnings, totalBookings, todayBookings, todayEarning;
    ImageView editImg;
    ImageView tickImg;
    LinearLayout manageInventoryLL;
    boolean isEditing = false;

    void initialize() {
        manageInventoryLL = findViewById(R.id.manageInventoryLL);
        todayEarning = findViewById(R.id.todayEarning);
        totalEarnings = findViewById(R.id.totalEarnings);
        totalBookings = findViewById(R.id.totalBookings);
        todayBookings = findViewById(R.id.todayBookings);
        userName = findViewById(R.id.userName);
        stallImage = findViewById(R.id.stallImage);
        userMobileNumber = findViewById(R.id.userMobileNumber);
        userAddress1 = findViewById(R.id.userAddress1);
        userAddress2 = findViewById(R.id.userAddress2);
        backImg = findViewById(R.id.backImg);
        editImg = findViewById(R.id.editImg);
        tickImg = findViewById(R.id.tickImg);
        headerTxt = findViewById(R.id.headerTxt);
        statusBtn = findViewById(R.id.statusBtn);
        headerTxt.setText("Profile Details");
        editImg.setOnClickListener(this);
        backImg.setOnClickListener(this);
        tickImg.setOnClickListener(this);
//        statusBtn.setOnClickListener(this);
        manageInventoryLL.setOnClickListener(this);
        disableEditText(userMobileNumber);
        showHide(false);
    }

    void showHide(boolean edit) {
        try {
            if (!edit) {
                disableEditText(userName);
                disableEditText(userAddress1);
                disableEditText(userAddress2);
//                tickImg.setVisibility(View.GONE);
//                editImg.setVisibility(View.VISIBLE);

            } else {

//                editImg.setVisibility(View.GONE);
//                tickImg.setVisibility(View.VISIBLE);
                enableEditText(userName);
                enableEditText(userAddress1);
                enableEditText(userAddress2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
//        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    boolean isValid() {
        try {
            if (userName.getText().toString().trim().length() == 0) {
                userName.requestFocus();
                Toast.makeText(mActivity, "Please enter your name.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (userAddress1.getText().toString().trim().length() == 0) {
                userAddress1.requestFocus();
                Toast.makeText(mActivity, "Please enter your address.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.statusBtn:

                updateProfileStatus("online");
                break;

            case R.id.manageInventoryLL:
                startActivity(new Intent(mActivity, StallInventory.class)
                        .putExtra("stall_id", stall_id));
                break;
            case R.id.backImg:
                finish();
                break;
        }
    }


    public void updateProfileStatus(String status) {
        String data = new GlobalAppApis().sendProfileStatus(this);
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getGeneralSettings(Constants.getAPIToken(mActivity), data);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {

                } else {
                    Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            }
        }, mActivity, call, "", true);


    }
}
