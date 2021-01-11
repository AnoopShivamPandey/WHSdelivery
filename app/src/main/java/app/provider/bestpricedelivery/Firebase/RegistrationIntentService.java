package app.provider.bestpricedelivery.Firebase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import app.provider.bestpricedelivery.Constants.Constants;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            if (token != null && token.length() > 0) {
                Constants.savePreferences(getApplicationContext(), Constants.FIREBASE_TOKEN, token);
            }
            Log.i(TAG, "FCM Registration Token: " + token);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}