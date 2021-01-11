package app.provider.bestpricedelivery.retrofit;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConnectToRetrofit {
    private String action = "";
    private Activity context;
    private RetrofitCallBackListenar callbacklistner;
    private boolean showDialogue = true;
    private long beginTime;
    private Call<String> call;
    private ProgressDialog dialog;

    public ConnectToRetrofit(RetrofitCallBackListenar callbacklistner, Activity context,
                             Call<String> _call, String action, boolean showDialogue) {
        this.callbacklistner = callbacklistner;
        this.context = context;
        this.action = action;
        this.showDialogue = showDialogue;
        this.call = _call;
        beginTime = System.currentTimeMillis();
        geData();
    }

    private void geData() {

        if (showDialogue && context != null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please wait");
            dialog.show();
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if ((dialog != null) && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d("DEBUG", "onResponse : " + response.raw() +
                            "Run time=" + (System.currentTimeMillis() - beginTime));
//                    ByteArrayInputStream bais = new ByteArrayInputStream(((String) response.body()).bytes());
//                    InputStreamReader reader = new InputStreamReader(bais);
//                    BufferedReader in = new BufferedReader(reader);
//                    String result = in.readLine();
                    if (response.raw().code() == 504) {
                        Toast.makeText(context, "ZTime out error", Toast.LENGTH_LONG).show();
                    } else if (response.raw().code() == 500) {
                        Toast.makeText(context, "server error", Toast.LENGTH_LONG).show();
                    }
                    Log.d("DEBUG", "onResponse : " + response.body());
                    if (callbacklistner != null) {
                        callbacklistner.RetrofitCallBackListenar(response.body(), action);
                    } else {
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (callbacklistner != null && (dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.d("DEBUG", "onFailure" + t.fillInStackTrace() + "MESSAGE : " + t.getMessage());
                if (t.getMessage() != null && t.getMessage().equalsIgnoreCase("Canceled")) {
                    //do nothing
                    Log.d("DEBUG", "onFailure request forcefully Canceled ");
                } else {
                    Toast.makeText(context, getError(t), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private static String getError(Throwable throwable) {
        String errMsg;
        if (throwable instanceof UnknownHostException || throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException || throwable instanceof ConnectTimeoutException
                || throwable instanceof IOException) {
            errMsg = "Check your connection and try again";
        } else if (throwable instanceof NetworkErrorException) {
            // Server error
            errMsg = "Unknown server error";
        } else {
            // NumberFormatException, ParseError, IllegalArgumentException, NullPointerException...
            errMsg = "There was an application error";
        }
        return errMsg;
    }

}


