package app.provider.bestpricedelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.Firebase.RegistrationIntentService;
import app.provider.bestpricedelivery.Tender.MainActivityForTender;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class Login extends AppCompatActivity {
    TextView leftTv, mainTv, rtTv, resendotpTv, loginTv, otpTextTv;
    ImageView drpIv;
    //    View vw;
    EditText mobEt;
    Activity mActivity;
    String logintype = "stall";
    LinearLayout bottomLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mActivity = this;
        logintype = getIntent().getStringExtra("logintype");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        leftTv = findViewById(R.id.leftTv);
        otpTextTv = findViewById(R.id.otpTextTv);
        mobEt = findViewById(R.id.mobEt);
        mainTv = findViewById(R.id.mainTv);
        bottomLl = findViewById(R.id.bottomLl);
        rtTv = findViewById(R.id.rtTv);
        drpIv = findViewById(R.id.drpIv);
        loginTv = findViewById(R.id.loginTv);
        resendotpTv = findViewById(R.id.sendotpTv);
        mobEt.setHint("Enter Password");

//        otpTextTv.setText(R.string.otpstring);
//        vw = findViewById(R.id.vw);
        rtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rtTv.setVisibility(View.GONE);
                leftTv.setVisibility(View.VISIBLE);
                leftTv.setText("Entity");
                mainTv.setText("Delivery Staff");
                logintype = "Worker";
                drpIv.setImageResource(R.mipmap.arrow_b);
//                vw.setBackgroundColor(getResources().getColor(R.color.brwn));
                bottomLl.setBackgroundDrawable(getResources().getDrawable(R.mipmap.brownline));
                resendotpTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_bg));
                resendotpTv.setTextColor(getResources().getColor(R.color.white));
            }
        });
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftTv.setVisibility(View.GONE);
                rtTv.setVisibility(View.VISIBLE);
                mainTv.setText("Entity");
                rtTv.setText("Delivery Staff");
                logintype = "Employer";
                drpIv.setImageResource(R.mipmap.yelllo_arrow);
//                vw.setBackgroundColor(getResources().getColor(R.color.ylotp));
                bottomLl.setBackgroundDrawable(getResources().getDrawable(R.mipmap.yelloline));
                resendotpTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.yellw_oval_bg));
                resendotpTv.setTextColor(getResources().getColor(R.color.blk));

            }
        });
        resendotpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendotp();
            }
        });
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid())
                    validate();

            }
        });

        if (logintype.equalsIgnoreCase("Employer")) {
            leftTv.performClick();
            rtTv.setEnabled(false);
            rtTv.setClickable(false);

        } else {
            rtTv.performClick();
            leftTv.setEnabled(false);
            leftTv.setClickable(false);
        }
    }


    boolean isValid() {
        if (mobEt.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, "Please enter password.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobEt.getText().toString().trim().length() < 3) {
            Toast.makeText(mActivity, "Please enter valid password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void validate() {
        String dataatoapss = new GlobalAppApis().validateOtp(mobEt.getText().toString(),
                getIntent().getStringExtra("mobileno"), logintype, this);
        ApiService client = getApiClient_ByPost();
        Call<String> call;
        if(logintype.equalsIgnoreCase("employer"))
        {
            call = client.validate_otprestaurant(dataatoapss);
        }
        else
        {
           call = client.validate_otp(dataatoapss);
        }
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {

                    if (logintype.equalsIgnoreCase("worker")) {
                        Constants.setuserType(mActivity, "stall");
                        Constants.setStallID(mActivity, jsonObject.getJSONObject("data").getString("id"));
                        Constants.setAPIToken(mActivity, jsonObject.getJSONObject("data").getString("api_token"));
                        Constants.setProfileImage(mActivity, jsonObject.getJSONObject("data").getString("profile_picture"));
//                        updateProfileStatus("online");
//                        Constants.savePreferences(mActivity,"qrcode",jsonObject.getJSONObject("data").getString("qrcode"));
//                        Constants.savePreferences(mActivity,"qimage",jsonObject.getJSONObject("data").getString("qrcode_image"));
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();


                    } else {
                        Constants.saveTenderDetails(mActivity, jsonObject);
                        Constants.setuserType(mActivity, "tender");
                        Constants.setTenderId(mActivity, jsonObject.getJSONObject("data").getString("id"));
                        Constants.setAPIToken(mActivity, jsonObject.getJSONObject("data").getString("api_token"));
                        Constants.setProfileImage(mActivity, jsonObject.getJSONObject("data").getString("profile_picture"));

                        Intent intent = new Intent(mActivity, MainActivityForTender.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                } else
                    Toast.makeText(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


            }
        }, Login.this, call, "", true);


    }

    public void sendotp() {
        String otp = new GlobalAppApis().sendOtp(getIntent().getStringExtra("mobileno"),
                logintype, this);
        ApiService client = getApiClient_ByPost();
        Call<String> call;
        if(logintype.equalsIgnoreCase("employer"))
        {
            call = client.sendotprestaurant(otp);
        }
        else
        {
            call = client.sendotp(otp);
        }
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);

            }
        }, Login.this, call, "", true);


    }


}
