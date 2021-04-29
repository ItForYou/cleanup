package co.kr.itforone.cleanup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static MediaPlayer mediaPlayer;//미디어 변수 선언
    private static final String TAG = "MyFirebaseMsgService";
    static public int pushId=0;
//    private String sound ;

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
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        sendNotification(remoteMessage);

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
        Log.d(TAG, "Refreshed " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]



    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
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
     */
    private void sendNotification(RemoteMessage remote) {
        String messageBody = remote.getData().get("message");
        String subject = remote.getData().get("subject");
        String channel_id = remote.getData().get("channel_id");
//        String getSound =remote.getData().get("sound");
        String goUrl = remote.getData().get("goUrl");
        String channelId = "cleanupFcm";
        String channelId_cancel = "cleanupFcm_cancel";
        String gubun = remote.getData().get("gubun");
        String viewUrl = remote.getData().get("viewUrl");



        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("goUrl", goUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Bitmap BigPictureStyle = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        long vibrate[] = {500, 0, 500, 0};
        /**/
//        Log.d("subject_test",subject);
//        if (subject.equals(str)){
//            sound = "/raw/cancel";
//        }else{
//            sound = "/raw/service";
//        }

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        Uri SoundUri= Uri.parse("android.resource://co.kr.itforone.cleanup/" + R.raw.service);
//        Log.d("sound_test", String.valueOf(SoundUri));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //접수일때
            if (channel_id.equals("cleanupFcm") ) {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(subject)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setVibrate(vibrate)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(pushId /* ID of notification */, notificationBuilder.build());
            }else if(channel_id.equals("cleanupFcm_cancel")){
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId_cancel)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(subject)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setVibrate(vibrate)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                notificationManager.notify(pushId /* ID of notification */, notificationBuilder.build());
            }else{
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this,"default")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(subject)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setVibrate(vibrate)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                notificationManager.notify(pushId /* ID of notification */, notificationBuilder.build());
            }


//            notificationManager.notify(pushId /* ID of notification */, notificationBuilder.build());
            pushId++;

        //sdk 26 이하일 때 채널 안해줘도 되서 setsound
        }else{

            NotificationManager notificationManager;
            PendingIntent intent2 = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = null;
            try {
                if (channel_id.equals("cleanupFcm") ) {
                    Uri SoundUri = Uri.parse("android.resource://co.kr.itforone.cleanup/" + R.raw.service);
                    builder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.BADGE_ICON_NONE)
                            .setContentTitle(subject)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(SoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(intent2);
                }else if (channel_id.equals("cleanupFcm_cancel")){
                    Uri SoundUri = Uri.parse("android.resource://co.kr.itforone.cleanup/" + R.raw.cancel);
                    builder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.BADGE_ICON_NONE)
                            .setContentTitle(subject)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(SoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(intent2);
                }else{
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.BADGE_ICON_NONE)
                            .setContentTitle(subject)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(intent2);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(pushId, builder.build());
            pushId++;
        }


    }

}
