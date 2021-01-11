package app.provider.bestpricedelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;


/**
 * Created by ASUS on 04-10-2018.
 */

public class OtpScreen extends AppCompatActivity {
    TextView leftTv, mainTv, rtTv, sendotpTv;
    ImageView drpIv;
//    View vw;
    EditText mobEt;
    String logintype = "employer";
    String mobileno = "";
    LinearLayout vwLl;
Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpscreen);
        mActivity = this;
        leftTv = findViewById(R.id.leftTv);
        mobEt = findViewById(R.id.mobEt);
        mainTv = findViewById(R.id.mainTv);
        rtTv = findViewById(R.id.rtTv);
        drpIv = findViewById(R.id.drpIv);
        sendotpTv = findViewById(R.id.sendotpTv);
        vwLl = findViewById(R.id.vwLl);
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
                vwLl.setBackgroundDrawable(getResources().getDrawable(R.mipmap.brownline));
                sendotpTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.oval_bg));
                sendotpTv.setTextColor(getResources().getColor(R.color.white));
                changeStausBsarcolor(R.color.colorPrimaryDark2);
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
                vwLl.setBackgroundDrawable(getResources().getDrawable(R.mipmap.yelloline));
//                drpIv.setImageResource(R.mipmap.yelllo_arrow);
//                vw.setBackgroundColor(getResources().getColor(R.color.ylotp));
                sendotpTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.yellw_oval_bg));
                sendotpTv.setTextColor(getResources().getColor(R.color.blk));
                changeStausBsarcolor(R.color.colorPrimaryDark);

            }
        });
        sendotpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mobEt.getText().toString().trim().length()==0)
                {
                    Toast.makeText(mActivity,"Please enter mobile number.",Toast.LENGTH_SHORT).show();
                }
                else if(mobEt.getText().toString().trim().length()<10)
                {
                    Toast.makeText(mActivity,"Please enter valid mobile number.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                  sendotp();
                }
            }
        });
    }

    public void sendotp() {
        String otp = new GlobalAppApis().sendOtp(mobEt.getText().toString(),logintype,this);
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
            //    Toast.makeText(getApplicationContext(),"Response"+result,Toast.LENGTH_LONG).show();
                if (jsonObject.getBoolean("status")) {
                    mobileno = mobEt.getText().toString();
                    Intent loginInt = new Intent(OtpScreen.this, Login.class);
                    loginInt.putExtra("logintype", logintype);
                    loginInt.putExtra("mobileno", mobileno);
                    startActivity(loginInt);
                    finish();
                } else {
                    mobileno = "";
                    Toast.makeText(OtpScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }, OtpScreen.this, call, "", true);
    }

    public void changeStausBsarcolor(int colorPrimaryDark) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(colorPrimaryDark);
        }

    }


}




