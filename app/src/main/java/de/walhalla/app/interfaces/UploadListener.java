package de.walhalla.app.interfaces;

public interface UploadListener {
    default void onDatabaseUploadDone(boolean successful) {
    }

    default void onPictureUploadDone(boolean successful, String idOfPicture) {
    }
}