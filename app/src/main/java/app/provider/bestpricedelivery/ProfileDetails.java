package app.provider.bestpricedelivery;
import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;
public class ProfileDetails extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;
    TextView walletBalance;
    RatingBar myRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        mActivity = this;
        initialize();
        System.out.println("==WORKER DEtAILS " + Constants.getWorkersDetails(mActivity));
    }

    EditText userName, userMobileNumber, userAddress1, userAddress2;
    ImageView backImg;
    TextView headerTxt;
    TextView statusBtn;
    TextView ratingUser;
    ImageView editImg;
    ImageView tickImg, stallImage;
    boolean isEditing = false;

    void initialize() {
        userName = findViewById(R.id.userName);
        ratingUser = findViewById(R.id.ratingUser);
        walletBalance = findViewById(R.id.walletBalance);
        userMobileNumber = findViewById(R.id.userMobileNumber);
        userAddress1 = findViewById(R.id.userAddress1);
        userAddress2 = findViewById(R.id.userAddress2);
        backImg = findViewById(R.id.backImg);
        editImg = findViewById(R.id.editImg);
        tickImg = findViewById(R.id.tickImg);
        headerTxt = findViewById(R.id.headerTxt);
        statusBtn = findViewById(R.id.statusBtn);
        totalBookings = findViewById(R.id.totalBookings);
        todayBookings = findViewById(R.id.todayBookings);
        totalEarnings = findViewById(R.id.totalEarnings);
        todayEarning = findViewById(R.id.todayEarning);
        stallImage = findViewById(R.id.stallImage);
        headerTxt.setText("Profile Details");
        editImg.setOnClickListener(this);
        tickImg.setOnClickListener(this);
        backImg.setOnClickListener(this);
        statusBtn.setOnClickListener(this);
        editImg.setVisibility(View.GONE);
        showHide(false);
        disableEditText(userMobileNumber);
        updateDetails();
    }

    TextView todayEarning, totalEarnings, todayBookings, totalBookings;

    void updateDetails() {
        try {


            JSONObject json = new JSONObject(Constants.getWorkersDetails(mActivity));
            System.out.println("++JSON " + json);
            JSONObject resultJSON = json.getJSONObject("data").getJSONObject("data");
            if (resultJSON.has("profile_picture") && resultJSON.getString("profile_picture").length() > 0) {
                Glide.with(mActivity)
                        .load(resultJSON.getString("profile_picture"))
                        .apply(new RequestOptions().error(R.drawable.app_logo).placeholder(R.drawable.app_logo))
                        .into(stallImage);
            }
            userName.setText(resultJSON.has("full_name") ?
                    resultJSON.getString("full_name") : "N/A");
            userMobileNumber.setText(resultJSON.has("mobile") ?
                    "+91 " + resultJSON.getString("mobile") : "N/A");
            totalBookings.setText(json.getJSONObject("data").getJSONObject("totalOrder").getString("totalOrder"));
            todayBookings.setText(json.getJSONObject("data").getJSONObject("totalOrder").getString("todayOrder"));
            if (resultJSON.has("total_rating")) {
                ratingUser.setText(resultJSON.getString("total_rating").length() > 0 ?
                        resultJSON.getString("total_rating") + "/5" :
                        "0/5");
            } else {
                ratingUser.setText("0/5");

            }
            totalEarnings.setText(getResources().getString(R.string.rupee) + resultJSON.getString("total_earning"));
            if (resultJSON.has("total_balance"))
                walletBalance.setText(getResources().getString(R.string.rupee) + resultJSON.getString("total_balance"));
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


    void showHide(boolean edit) {
        try {
            if (!edit) {
                disableEditText(userName);
                disableEditText(userAddress1);
                disableEditText(userAddress2);
                tickImg.setVisibility(View.GONE);
                editImg.setVisibility(View.GONE);

            } else {

                editImg.setVisibility(View.GONE);
                tickImg.setVisibility(View.VISIBLE);
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
            case R.id.editImg:
                showHide(true);
                break;
            case R.id.tickImg:
                if (isValid()) {
                    showHide(false);
                }
                break;
            case R.id.backImg:
                finish();
                break;
            case R.id.statusBtn:
                if (statusBtn.getText().toString().trim().equalsIgnoreCase("online")) {
                    updateProfileStatus("offline");
                } else {
                    updateProfileStatus("online");
                }
                break;
        }
    }


    public void updateProfileStatus(final String status) {
        String data = new GlobalAppApis().sendProfileStatus(this);
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getGeneralSettings(Constants.getAPIToken(mActivity), data);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {
                    Constants.saveWorkersDetails(mActivity, jsonObject);
                    updateDetails();
                } else {
                    Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            }
        }, mActivity, call, "", true);


    }
}
