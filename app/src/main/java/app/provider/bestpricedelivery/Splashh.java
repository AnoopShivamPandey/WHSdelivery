package app.provider.bestpricedelivery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.Constants.PermissionUtill;
import app.provider.bestpricedelivery.Tender.MainActivityForTender;

/**
 * Created by ASUS on 29-09-2018.
 */

public class Splashh extends AppCompatActivity {
    VideoView videoPlayer;
    final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView splashImage = findViewById(R.id.splashImage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Glide.with(this).load(R.drawable.whitetext).into(splashImage);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtill.checkAndRequestGPSPermissions(Splashh.this, PermissionUtill.requestLocationPermission, REQUEST_PERMISSIONS)) {
                startNextScreen();
            }
        } else {
            startNextScreen();

        }}

    private void startNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Constants.isUserLoggedIn(Splashh.this)) {
                    Intent intent = new Intent(Splashh.this, OtpScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (Constants.getUserType(Splashh.this).equalsIgnoreCase("stall")) {
                        Intent intent = new Intent(Splashh.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Splashh.this, MainActivityForTender.class);
                        startActivity(intent);
                    }
                    finish();

                }
            }
        }, 3000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (!PermissionUtill.hasPermissions(Splashh.this, permissions)) {
                    if (PermissionUtill.hasRationalPermissions(Splashh.this, permissions)) {
                        PermissionUtill.openDialog(Splashh.this);
                    } else {
                        Toast.makeText(Splashh.this, "Please go to setting and enable permissions.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    startNextScreen();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
