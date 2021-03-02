package de.walhalla.app.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final long SENDER_ID = 159729181477L;
    public static final String SERVER_KEY = "AAAAJTCZ4yU:APA91bEq5Qao6e4_WMvS3eUk0BVJIom5Nrfky5gvLOs1K2xe6938E-_R4uVFmOBty5hDZsdS5L0OUHXlOrDQblNRZPg1lb9TH0NxDN5UFjAQaF3cMBqxBPudOWfLZeeWk8CkPsdsVYNB";

    // [START on_new_token]

    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    public static void sendUpstream(String title, String message) {
        final int messageId = 0; // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build());
        // [END fcm_send_upstream]
    }
}
