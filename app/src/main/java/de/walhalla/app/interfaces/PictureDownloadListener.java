package de.walhalla.app.interfaces;

import de.walhalla.app.threads.async.DownloadPicture;

public interface PictureDownloadListener {
    void notifyOfCompleteListener(final DownloadPicture AsyncTask);
}
