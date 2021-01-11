package app.provider.bestpricedelivery.Services;

import android.app.Activity;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

class CommonUtill {
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;


        return (dist * 1000);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            if (activity != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                View v = activity.getCurrentFocus();
                if (v != null) {
                    IBinder binder = activity.getCurrentFocus()
                            .getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}