package app.provider.bestpricedelivery;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import app.provider.bestpricedelivery.Constants.Constants;

public class GlobalAppApis {
    String sendOtp(String mobno, String loginType, Context mContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_no", mobno);
            jsonObject.put("fcm_token", Constants.getFirebaseToken(mContext));
            jsonObject.put("user_type", loginType.equalsIgnoreCase("employer") ? "restaurant" : "deliveryboy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    String validateOtp(String otp, String mobileno, String loginType, Context mContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("otp_text", otp);
            jsonObject.put("mobile_no", mobileno);
            jsonObject.put("user_type", loginType.equalsIgnoreCase("employer") ? "restaurant" : "deliveryboy");
            jsonObject.put("fcm_token", Constants.getFirebaseToken(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String sendBookingList(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deliveryboy_id", Constants.getStallId(context));
            jsonObject.put("page", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getBookingListForTender(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("restaurant_id", Constants.getTenderId(context));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getBookingDetails(String order_id, Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deliveryboy_id", Constants.getStallId(context));
            jsonObject.put("order_id", order_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getBookingDetailsForTender(String order_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getLogoutDetails(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getStallId(context));
//            jsonObject.put("user_type", "deliveryboy");/**/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getLogoutDetailsForTender(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getTenderId(context));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendBookingDetails(Context context, String order_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deliveryboy_id", Constants.getStallId(context));
            jsonObject.put("order_id", order_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendProfileStatus(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getStallId(context));
            jsonObject.put("latitude", Constants.getLatitude(context));
            jsonObject.put("longitude", Constants.getLongitude(context));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendAcceptRejectDetails(Context context, String orderId, String status, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getStallId(context));
            jsonObject.put("order_id", orderId);
            jsonObject.put("status", status);
            jsonObject.put("reason", msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendAcceptDetails(Context context, String orderId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("restaurant_id", Constants.getTenderId(context));
            jsonObject.put("order_id", orderId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendCancelledDetails(Context context, String orderId, String cancel_reason) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cancelled_by", Constants.getTenderId(context));
            jsonObject.put("order_id", orderId);
            jsonObject.put("cancel_reason", cancel_reason);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String sendAcceptRejectDetails_(Context context, String orderId, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getStallId(context));
            jsonObject.put("order_id", orderId);
            jsonObject.put("status", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getUserLocation(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Constants.getStallId(context));
            jsonObject.put("latitude", Constants.getLatitude(context));
            jsonObject.put("longitude", Constants.getLongitude(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getStallList(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tender_id", Constants.getTenderId(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getInventory(String stall_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("restaurant_id", stall_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
