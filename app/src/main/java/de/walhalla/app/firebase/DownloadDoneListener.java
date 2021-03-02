package de.walhalla.app.firebase;

import android.util.Log;

import de.walhalla.app.utils.Variables.Firebase;

public interface DownloadDoneListener {
    String TAG = "DownloadDoneListener";

    default void realtimeDatabase() {
        Log.i(TAG, "realtimeDatabase done");
        Firebase.setFirestoreDB();
    }

    default void firestoreDB() {
        Log.i(TAG, "FirestoreDB done");
        Firebase.setStorage();
    }

    default void cloudStorage() {
        Log.i(TAG, "CloudStorage done");
        Firebase.setAuth();
    }

    default void authentication() {
        Log.i(TAG, "authentication done");
        onDone();
    }

    void onDone();
}
