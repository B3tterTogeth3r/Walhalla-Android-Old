package de.walhalla.app.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static final long SENDER_ID = 159729181477L;
    public static final String SERVER_KEY = "AAAAJTCZ4yU:APA91bEq5Qao6e4_WMvS3eUk0BVJIom5Nrfky5gvLOs1K2xe6938E-_R4uVFmOBty5hDZsdS5L0OUHXlOrDQblNRZPg1lb9TH0NxDN5UFjAQaF3cMBqxBPudOWfLZeeWk8CkPsdsVYNB";
    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(@NotNull RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = Firebase.Messaging.TOPIC_DEFAULT;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_website)
                        .setContentTitle(remoteMessage.getData().get("Title"))
                        .setContentText(remoteMessage.getData().get("Content"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        try {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, "sendNotification: ", e);
        }
    }
}
