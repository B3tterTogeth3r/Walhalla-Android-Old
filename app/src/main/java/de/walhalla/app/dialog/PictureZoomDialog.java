package de.walhalla.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import de.walhalla.app.R;
import de.walhalla.app.utils.DownloadPictureQue;

public class PictureZoomDialog extends Dialog {
    Drawable drawable;
    String onlineName;

    public PictureZoomDialog(FragmentActivity a, Drawable drawable) {
        //Zoom an image, that is already downloaded.
        super(a);
        this.drawable = drawable;
    }

    public PictureZoomDialog(Context a, Drawable drawable) {
        //Zoom an image, that is already downloaded.
        super(a);
        this.drawable = drawable;
    }

    public PictureZoomDialog(FragmentActivity a, String name) {
        //Show recipe
        super(a);
        this.onlineName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Picture");
        setContentView(R.layout.dialog_picture_large);
        setCanceledOnTouchOutside(true);
        ImageView image = findViewById(R.id.largeImageView);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (drawable != null) {
            image.setImageDrawable(drawable);
        } else if (onlineName != null) {
            //TODO Download the picture from the firebase cloud
            /*DownloadPictureQue.addEntry(new DownloadPicture(onlinePath, onlineName, image));
            DownloadPictureQue.startAll();*/
        }
        image.setOnClickListener(v -> dismiss());
    }

    @Override
    public void cancel() {
        DownloadPictureQue.stopAll();
        super.cancel();
    }

    @Override
    public void dismiss() {
        DownloadPictureQue.stopAll();
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }
}