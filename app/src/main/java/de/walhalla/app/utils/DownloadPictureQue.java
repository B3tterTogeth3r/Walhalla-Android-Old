package de.walhalla.app.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.interfaces.PictureDownloadListener;
import de.walhalla.app.threads.async.DownloadPicture;

public class DownloadPictureQue extends AsyncTask<Void, Void, Bitmap> implements PictureDownloadListener {
    //private static String TAG = "PictureQue";
    public boolean internet = App.getInternet();
    public static ArrayList<DownloadPicture> que = new ArrayList<>();
    public static PictureDownloadListener listener;
    private static volatile boolean stopRunning = false;

    public DownloadPictureQue() {
        listener = this;
    }

    public static void addEntry(final DownloadPicture thread) {
        //Add a new Thread to the list to be started later.
        que.add(thread);
    }

    public static void removeEntry(final DownloadPicture thread) {
        //Remove the thread after it got stopped or finished on its own.
        que.remove(thread);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return null;
    }

    public static void stopAll() {
        //Stop all running picture downloads in the order they got started.
        stopRunning = true;
        int size = que.size();
        while (0 < size) {
            size--;
            que.get(0).cancel(true);
            que.remove(0);
        }
        stopRunning = false;
    }

    public static void startAll() {
        //Wait while the stop is running. It is just a precaution to prevent tasks from being started twice.

        while (stopRunning) {
        }
        if (que.size() != 0) {
            try {
                if (que.get(0).getStatus() == Status.PENDING) {
                    //Log.e(TAG, "que.size(): " + que.size());
                    que.get(0).execute();
                } else {
                    listener.notifyOfCompleteListener(que.get(0));
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void notifyOfCompleteListener(DownloadPicture AsyncTask) {
        //After a task finished, it is removed from the List and the next one is started
        removeEntry(AsyncTask);
        if (0 < que.size()) {
            if (que.get(0).getStatus() == Status.PENDING) {
                que.get(0).execute();
            } else {
                notifyOfCompleteListener(que.get(0));
            }
        }
    }
}
