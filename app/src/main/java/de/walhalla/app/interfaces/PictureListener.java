package de.walhalla.app.interfaces;

import android.graphics.Bitmap;

public interface PictureListener {
    void downloadDone(Bitmap imageBitmap);
    default void descriptionDone(String description){}
    default void nextImage(){}
    default void previousImage(){}
}
