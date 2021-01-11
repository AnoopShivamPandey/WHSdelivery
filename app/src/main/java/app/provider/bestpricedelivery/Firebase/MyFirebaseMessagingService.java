package app.provider.bestpricedelivery.Firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import app.provider.bestpricedelivery.Bookings.NewBooking;
import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.MainActivity;
import app.provider.bestpricedelivery.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
   Log.d(TAG, "From: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        switch (remoteMessage.getData().get("action").toLowerCase()) {
            case "neworder":
                try{
                    final MediaPlayer mp = MediaPlayer.create(this, R.raw.audiotone);
                    mp.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                try {
                    JSONObject resultJSON = new JSONObject(remoteMessage.getData().get("data"));
                    startActivity(new Intent(getApplicationContext(), NewBooking.class)
                            .putExtra("booking_id", resultJSON.getString("id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        try {
            if (token != null && token.length() > 0) {
                Constants.savePreferences(getApplicationContext(), Constants.FIREBASE_TOKEN, token);
            }
            Log.i(TAG, "FCM Registration Token: " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }


   /* public static void showNotification(Context context, int notificationType, Bundle notifyBundle) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;
        PendingIntent pendingIntent = null;
        if (notificationType == Constants.ACCEPT_NOTIFY) {
            CommonUtill.print("showNotification ==Constants.ACCEPT_NOTIFY");
            notificationIntent = new Intent(context, Searching_For_Driver_Now_Activity.class);
            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.START_RIDE) {
            CommonUtill.print("showNotification ==Constants.START_RIDE");
            notificationIntent = new Intent(context, Activity_Book.class);
            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.CANCEL_BOOKING) {
            CommonUtill.print("showNotification ==Constants.CANCEL_BOOKING");

//            try {
//                JSONObject userJson = new JSONObject(context.getSharedPreferences(Utils.TENVEHICLEPREF, MODE_PRIVATE).getString(Utils.USEREMAILDATA, ""));
//                CommonUtill.print("showNotification ==Constants.CANCEL_BOOKING" + userJson.getString("type"));
            if (notifyBundle.getBundle("data").getString("usertype").equalsIgnoreCase("customer")) {
                notificationIntent = new Intent(context, HomeScreenActivityDriver.class);
            } else {
                notificationIntent = new Intent(context, HomeScreenActivityCustomer.class);
            }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        } else if (notificationType == Constants.PROMO_CODE) {
            notificationIntent = new Intent(context, PromoActivity.class);
        } else if (notificationType == Constants.ARRIVED_NOTIFY) {
            CommonUtill.print("showNotification ==Constants.ARRIVED_NOTIFY");
            notificationIntent = new Intent(context, Activity_Book.class);
            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.IN_TRAFFIC_NOTIFY) {
            CommonUtill.print("showNotification ==Constants.IN_TRAFFIC_NOTIFY");
            notificationIntent = new Intent(context, Activity_Book.class);
            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.REQUEST_RIDE) {
            CommonUtill.print("showNotification ==Constants.REQUEST_RIDE");
            notificationIntent = new Intent(context, HomeScreenActivityDriver.class);
            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.CHECKOUT_NOTIFY) {
            CommonUtill.print("showNotification ==Constants.CHECKOUT_NOTIFY");
            notificationIntent = new Intent(context, Rate_Driver_Activity.class);
//            notificationIntent.putExtra("data", notifyBundle.getBundle("data"));
        } else if (notificationType == Constants.START_ADVANCE_RIDE) {
            CommonUtill.print("showNotification ==Constants.START_ADVANCE_RIDE");

            try {
                JSONObject userJson = new JSONObject(context.getSharedPreferences(Utils.TENVEHICLEPREF, MODE_PRIVATE).getString(Utils.USEREMAILDATA, ""));
                CommonUtill.print("showNotification ==Constants.START_ADVANCE_RIDE" + userJson.getString("type"));
                if (userJson.getString("type").equalsIgnoreCase("driver")) {
                    notificationIntent = new Intent(context, LocateRideDriver.class);
                } else {
                    notificationIntent = new Intent(context, Activity_Book.class);
                }
            } catch (JSONException e) {
                CommonUtill.printLog("Error:", Log.getStackTraceString(e));
            }

        }


        if (notificationIntent != null) {
            notificationIntent.putExtra("from", notificationType);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }


        if (notificationIntent != null && pendingIntent != null) {

            String channelId = "channel-01";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelName = "Channel Name";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            Notification notification = builder
                    .setTicker("CLQ ride")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("CLQ ride")
                    .setContentText(notifyBundle.getString("msg"))
                    .setContentIntent(pendingIntent)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .build();

            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration


            Uri path = null;
            if (notificationType == Constants.IN_TRAFFIC_NOTIFY) {
                path = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.in_trafic_new);
            } else if (notificationType == Constants.ARRIVED_NOTIFY || notificationType == Constants.ACCEPT_NOTIFY ||
                    notificationType == Constants.START_RIDE || notificationType == Constants.CANCEL_BOOKING ||
                    notificationType == Constants.CHECKOUT_NOTIFY) {
                path = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.arrival);
            } else if (notificationType == Constants.REQUEST_RIDE) {
                path = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.arrive_five);
            }


            if (path == null)
                notification.defaults |= Notification.DEFAULT_SOUND;
            else
                notification.sound = path;

//            if (notificationType!=Constants.CANCEL_BOOKING) {
//                Constants.DIALOG_TYPE = notificationType;
//            }
            notificationManager.notify(notificationType, notification);

        }

    }


    public static void Show_Normal_Notification(Context context, String msg, String notification_id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), Notification_Detail_Activity.class);
        intent.putExtra("Notification_Id", notification_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "channel-01";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String channelName = "Channel Name";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        Notification notification = builder
                .setTicker("CLQ ride")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("CLQ ride")
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();

        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound


        notificationManager.notify(0, notification);

    }
*/

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                        .setContentTitle(getString(R.string.fcm_message))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}