package de.walhalla.app.threads.async;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.Objects;

import de.walhalla.app.R;
import de.walhalla.app.utils.DownloadPictureQue;

@SuppressLint("StaticFieldLeak")
public class DownloadPicture extends DownloadPictureQue {
    String TAG = "DownloadPicture";
    ImageView image;
    String totalPath;
    Boolean running = false;

    public DownloadPicture(String path, String name, ImageView imageView) {
        this.image = imageView;
        this.totalPath = path + name + ".jpg";
    }

    @Override
    public Bitmap doInBackground(Void... voids) {
        running = (true);
        Thread.currentThread().setName(TAG + " " + totalPath);

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(totalPath).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG + " - Error", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
        running = (false);

        return mIcon11;
    }

    @Override
    protected void onCancelled() {
        try {
            throw new InterruptedException();
        } catch (InterruptedException ignored) {
        }
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        //Set the image into the View
        if (result != null) {
            image.setImageBitmap(result);
        } else {
            image.setImageResource(R.drawable.wappen_2017);
        }
        if (listener != null) {
            listener.notifyOfCompleteListener(this);
        }
    }
}