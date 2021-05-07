package de.walhalla.app.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;

public class Firebase {
    private final static String TAG = "Firebase";

    public interface Chargen {
        void currentChargen();
    }

    public interface Event {
        void oneSemester(int semester_id);
    }

    public interface board {
        void student(int semester_id);

        void philister(int semester_id);
    }

    public abstract static class Auth implements FirebaseAuth.AuthStateListener, FirebaseAuth.IdTokenListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            changer(firebaseAuth);
        }

        @Override
        public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
            //changer(firebaseAuth);
        }

        abstract void changer(@NonNull FirebaseAuth firebaseAuth);
    }

    public static class Messaging {
        private static final String TAG = "Firebase.Messaging";
        private static final String SENDER_ID = "159729181477";
        private static final ArrayList<String> SUBSCRIBED_TO = new ArrayList<>();
        public static String TOPIC_INTERNAL = "internal";
        public static String TOPIC_DEFAULT = "public";

        public static void SubscribeTopic(String topic) {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        String msg = App.getContext().getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = App.getContext().getString(R.string.msg_subscribe_failed);
                        } else {
                            SUBSCRIBED_TO.add(topic);
                        }
                        Log.d(TAG, "SubscribeTopic: " + topic + ": " + msg);
                        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }

        public static void UnsubscribeTopic(String topic) {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(topic)
                    .addOnCompleteListener(task -> {
                        String msg = App.getContext().getString(R.string.msg_unsubscribed);
                        if (!task.isSuccessful()) {
                            msg = App.getContext().getString(R.string.msg_unsubscribe_failed);
                        } else {
                            SUBSCRIBED_TO.remove(topic);
                        }
                        Log.d(TAG, "UnsubscribeTopic: " + topic + ": " + msg);
                        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
                    });
        }

        public static boolean isSubscribed(String topic) {
            return SUBSCRIBED_TO.contains(topic);
        }
    }
}
