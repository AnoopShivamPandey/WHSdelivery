package app.provider.bestpricedelivery.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Map;


public class Constants {
    public static final String emailExpression = "^[a-z+0-9!#$%&'*/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$)";
    public static final String nricExpression = "^[STFG]\\d{7}[A-Z]$/)";
    public static final String PREF_KEY = "MyTreatPref";
    public static final int CAMERA_REQUEST_CODE = 1010;
    public static final int RESULT_LOAD_IMAGE = 101;
    public static final int CROP_IMAGE = 10011;
    public static final String ERROR = "error";
    public static final String TERMSCONDITIONS = "termsCondition";
    public static final String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
    public static final String Authorztn_Key = "Authorztn_Key";
    private static final String USER_EWALLET_BALANCE = "USER_EWALLET_BALANCE";
    public static String LOGIN_ATTEMPTS = "LOGIN_ATTEMPTS";
    public static String USER_CONFIGURATION = "USER_CONFIGURATION";
    public static String USER_TOKEN = "user_token";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_EMAIL = "user_email";
    public static String USER_FAVORITE_DOCTOR = "fav_doctor";
    public static String DOCTOR_REJECT_COUNT = "doc_reject_count";
    public static String PATIENT_NOTIFICATION_COUNT = "patient_notification_count";
    public static String NOTIFICATION_COUNT = "notification_count";
    public static String VIDEO_CALL_SCREEN = "video_call_screen";
    public static String RECAPTCHA_COUNT = "0";
    public static String FIREBASE_TOKEN = "firebase_token";
    public static String USER_PROFILE_IMAGE = "USER_PROFILE_IMAGE";
    public static String USER_CARD_DETAILS = "USER_CARD_DETAILS";
    public static String USER_STRIPE_ID = "USER_STRIPE_ID";
    public static JSONObject signUpJson;
    public static String ALLOW_VIDEO_CALL_POPUP = "ALLOW_VIDEO_CALL_POPUP";
    public static String SEARCHED_DOCTOR = "SEARCHED_DOCTOR";
    public static String USER_ADDRESS = "USER_ADDRESS";
    public static String DOCTOR_SPECIALITY_LIST = "DOCTOR_SPECIALITY_LIST";
    public static String GET_ORGANIZATION_LIST = "GET_ORGANIZATION_LIST";
    private static String STALL_ID = "STALL_ID";
    private static String TENDER_ID = "TENDER_ID";
    private static String API_TOKEN = "API_TOKEN";
    private static String PROFILE_PIC = "PROFILE_PIC";
    public static String LATITUDE = "LATITUDE";
    public static String LONGITUDE = "LONGITUDE";
    public static String ONLINE_STATUS = "ONLINE_STATUS";
    public static String WORKER_BOOKING_DETAILS = "WORKER_BOOKING_DETAILS";
    public static String TENDER_BOOKING_DETAILS = "WORKER_BOOKING_DETAILS";
    public static String USER_TYPE = "USER_TYPE";

    public static boolean savePreferences(Context c, String key, String value) {
        SharedPreferences sp = initializeSharedPreferences(c);
        if (sp != null) {
            Editor editor = sp.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static JSONObject initializeSignUpJson() {
        if (signUpJson == null) {
            signUpJson = new JSONObject();
        }
        return signUpJson;
    }

    public static void clearSignUpJson() {
        signUpJson = new JSONObject();
    }

    public static String getSavedPreferences(Context c, String key,
                                             String defValue) {
        SharedPreferences sp = initializeSharedPreferences(c);
        if (sp != null) {
            return sp.getString(key, defValue);
        }
        return defValue;
    }

    public static Map<String, ?> getAllPreferences(Context c) {
        SharedPreferences sp = initializeSharedPreferences(c);
        return sp.getAll();
    }

    private static SharedPreferences initializeSharedPreferences(Context c) {
        if (c != null)
            return c.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE);
        return null;
    }

    public static boolean clearSavedPreferences(Context c, String key) {
        SharedPreferences sp = initializeSharedPreferences(c);
        Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean clearAllSavedPreferences(Context c) {
        SharedPreferences sp = initializeSharedPreferences(c);
        if (sp != null) {
            Editor editor = sp.edit().clear();
            return editor.commit();
        }
        return false;
    }

    public static boolean isUserLoggedIn(Context context) {
        if (Constants.getSavedPreferences(context, Constants.API_TOKEN, "")
                .length() > 0) {
            return true;
        }
        return false;
    }


    public static String getUserName(Context context) {
        if (Constants.getSavedPreferences(context, Constants.USER_NAME, "")
                .length() > 0) {
            return Constants.getSavedPreferences(context, Constants.USER_NAME, "");
        }
        return "";
    }


    public static String getTotalUnreadNotificationCount(Context context) {
        if (Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "") == null ||
                Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "").length() == 0) {
            Constants.savePreferences(context, Constants.NOTIFICATION_COUNT, "0");
        }
        System.out.println("UNREAD NOTIFICATION " + Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, ""));
        return Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "");
    }

    public static void updateTotalUnreadNotificationCount(Context context) {
        if (Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "") == null ||
                Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "").length() == 0) {
            Constants.savePreferences(context, Constants.NOTIFICATION_COUNT, "0");
        }
        int n = 1 + Integer.parseInt(Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, ""));
        Constants.savePreferences(context, Constants.NOTIFICATION_COUNT, n + "");
    }

    public static void resetTotalUnreadNotificationCount(Context context) {
        int n = Integer.parseInt(Constants.getSavedPreferences(context, Constants.NOTIFICATION_COUNT, "")) - 1;
        Constants.savePreferences(context, Constants.NOTIFICATION_COUNT, n + "");
    }


    public static String getUserToken(Context context) {
        if (Constants.getSavedPreferences(context, Constants.USER_TOKEN, "")
                .length() > 0) {
            return Constants.getSavedPreferences(context, Constants.USER_TOKEN, "");
        }
        return "";
    }

    public static String getUserID(Context context) {
        if (Constants.getSavedPreferences(context, Constants.USER_ID, "")
                .length() > 0) {
            return Constants.getSavedPreferences(context, Constants.USER_ID, "");
        }
        return "";
    }

    public static String getFirebaseToken(Context context) {
        if (Constants.getSavedPreferences(context, Constants.FIREBASE_TOKEN, "")
                .length() > 0) {
            return Constants.getSavedPreferences(context, Constants.FIREBASE_TOKEN, "");
        } else {
            return FirebaseInstanceId.getInstance().getToken();
        }
    }


    public static void setStallID(Context mContext, String stallID) {
        savePreferences(mContext, STALL_ID, stallID);
    }

    public static void setAPIToken(Context mContext, String APIToken) {
        savePreferences(mContext, API_TOKEN, APIToken);
    }

    public static String getStallId(Context mContext) {
        return getSavedPreferences(mContext, STALL_ID, "");
    }

    public static String getAPIToken(Context mContext) {
        return getSavedPreferences(mContext, API_TOKEN, "");
    }

    public static void setProfileImage(Context mContext, String imageUrl) {
        savePreferences(mContext, PROFILE_PIC, imageUrl);
    }

    public static String getProfileImage(Context mContext) {
        return getSavedPreferences(mContext, PROFILE_PIC, "");
    }

    public static double getLatitude(Context mContext) {
        String lat = getSavedPreferences(mContext, LATITUDE, "");
        if (lat.length() > 0 && !lat.toLowerCase().contains("lat")) {
            return Double.parseDouble(lat);
        } else {
            return 0;
        }
    }

    public static double getLongitude(Context mContext) {
        String lat = getSavedPreferences(mContext, LONGITUDE, "");
        if (lat.length() > 0 && !lat.toLowerCase().contains("lon")) {
            return Double.parseDouble(lat);
        } else {
            return 0;
        }
    }

    public static void updateLocation(Context context, Location location) {
        savePreferences(context, LATITUDE, location.getLatitude() + "");
        savePreferences(context, LONGITUDE, location.getLongitude() + "");
    }

    public static void setOnlineStatus(Context context, String status) {
        savePreferences(context, ONLINE_STATUS, status);
    }

    public static String getOnlineStatus(Context context) {
        return getSavedPreferences(context, ONLINE_STATUS, "");
    }

    public static void sendSMSDialog(String phoneNumber, Context context) {
        try {
            String uri = null;
            uri = "smsto:" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
            intent.putExtra("sms_body", "");
            intent.putExtra("compose_mode", true);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openDiallerPad(String phoneNumber, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveWorkersDetails(Context context, JSONObject jsonObject) {
        savePreferences(context, WORKER_BOOKING_DETAILS, jsonObject.toString());
    }

    public static String getWorkersDetails(Context context) {
        return getSavedPreferences(context, WORKER_BOOKING_DETAILS, "");
    }

    public static void saveTenderDetails(Context context, JSONObject jsonObject) {
        savePreferences(context, TENDER_BOOKING_DETAILS, jsonObject.toString());
    }

    public static String getTenderDetails(Context context) {
        return getSavedPreferences(context, TENDER_BOOKING_DETAILS, "");
    }

    public static void openGoogleMapNaviagtion(Context context, double startLat, double startLong) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLong + "" +
                            "&daddr=" + Constants.getLatitude(context) + "," + Constants.getLongitude(context)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void SOP(String s) {
        System.out.println(s);
    }

    public static void setTenderId(Context mContext, String tenderId) {
        savePreferences(mContext, TENDER_ID, tenderId);
    }

    public static String getTenderId(Context mContext) {
        return getSavedPreferences(mContext, TENDER_ID, "");
    }

    public static void setuserType(Context mContext, String userType) {
        savePreferences(mContext, USER_TYPE, userType);
    }

    public static String getUserType(Context mContext) {
        return getSavedPreferences(mContext, USER_TYPE, "");
    }
}