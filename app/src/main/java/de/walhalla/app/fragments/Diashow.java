package de.walhalla.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.PictureListener;
import de.walhalla.app.interfaces.StopDiashowListener;
import de.walhalla.app.utils.ImageDownload;
import de.walhalla.app.utils.Variables;

public class Diashow implements View.OnClickListener, StopDiashowListener, PictureListener {
    private static final String TAG = "Diashow";
    public static StopDiashowListener listener;
    public static boolean threadAlive = false;
    private final Context context;
    private final Animation anim;
    private final AtomicInteger diashow_position = new AtomicInteger(0);
    private final ArrayList<Thread> threads = new ArrayList<>();
    private RelativeLayout layout;
    private TextView diashow_description;
    private ImageView diashow;
    private ImageButton diashow_right, diashow_left;
    private ArrayList<String> picture_names;
    private String threadName;
    private ProgressBar progressBar;

    public Diashow(Context context) {
        this.context = context;
        listener = this;
        anim = AnimationUtils.loadAnimation(context, R.anim.picture_change);
    }

    @SuppressLint("InflateParams")
    public RelativeLayout show(String documentReference) {
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (RelativeLayout) inflater.inflate(R.layout.diashow, null);

        DocumentReference reference = Variables.Firebase.FIRESTORE.collection("Diashow").document(documentReference);
        load(reference);
        return layout;
    }

    @SuppressLint("InflateParams")
    public RelativeLayout show(@NotNull DocumentReference documentReference) {
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (RelativeLayout) inflater.inflate(R.layout.diashow, null);

        load(documentReference);

        return layout;
    }

    private void load(@NotNull DocumentReference documentReference) {
        threadName = documentReference.toString();

        diashow_left = layout.findViewById(R.id.diashow_previous);
        diashow_right = layout.findViewById(R.id.diashow_next);
        diashow = layout.findViewById(R.id.diashow_image);
        diashow_description = layout.findViewById(R.id.diashow_description);
        progressBar = layout.findViewById(R.id.diashow_progressBar);

        diashow_right.setClickable(false);
        diashow_left.setClickable(false);
        diashow_right.setOnClickListener(this);
        diashow_left.setOnClickListener(this);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                picture_names = (ArrayList<String>) documentSnapshot.get("picture_names");
                if (picture_names != null && picture_names.size() != 0) {
                    //download only the first image
                    try {
                        layout.startAnimation(anim);
                    } catch (Exception ignored) {
                    }
                    diashow_position.set(0);
                    downloadImage(picture_names.get(diashow_position.get()));

                    if (picture_names.size() == 1) {
                        diashow_right.setVisibility(View.GONE);
                        diashow_left.setVisibility(View.GONE);
                    } else {
                        diashow_right.setVisibility(View.VISIBLE);
                        diashow_left.setVisibility(View.VISIBLE);
                        //Automatic switch between images after 3-5 seconds.
                        startSwitchTimer();
                        //TODO Activate swipe gestures
                    }
                }
            }
        });
    }

    /**
     * Automatic switch between images after 10 seconds.
     */
    private void startSwitchTimer() {
        threadAlive = true;
        Thread thread = new Thread();//ImageDownload.diashowTimer(this, picture_names, threadName));
        thread.setName(threadName);
        threads.add(thread);
        //TODO How to stop multiple diashow elements inside one fragment? While there is a problem, no automatic diashow should start.
        //thread.start();
    }

    @Override
    public void onClick(@NotNull View v) {
        //Diashow button left got clicked
        if (v.getId() == diashow_left.getId()) {
            previousImage();
        }
        //Diashow button right got clicked
        else if (v.getId() == diashow_right.getId()) {
            nextImage();
        }
    }

    private void downloadImage(String image_name) {
        new ImageDownload(this, image_name, true, true).execute();
    }

    @Override
    public synchronized void stopDiashow() {
        //Stop the automatic new images
        for (Thread thread : threads) {
            if (thread.isAlive() | threadAlive) {
                threadAlive = false;
                try {
                    thread.interrupt();
                    if (thread.isInterrupted()) {
                        Log.d(TAG, "Thread successfully interrupted " + thread.getName());
                    }
                } catch (Exception ignored) {
                }
            }
            threads.remove(thread);
        }
    }

    @Override
    public void downloadDone(Bitmap imageBitmap) {
        //Animate a blend from one to the next image
        try {
            diashow.startAnimation(anim);
            diashow.setImageBitmap(imageBitmap);
            diashow_right.setClickable(true);
            diashow_left.setClickable(true);
        } catch (Exception e) {
            Log.e(TAG, "downloadDone: couldn't display the downloaded image", e);
        }
    }

    @Override
    public void descriptionDone(String description) {
        if (description != null && description.length() != 0) {
            diashow.startAnimation(anim);
            diashow_description.setVisibility(View.VISIBLE);
            diashow_description.setText(description);
        } else {
            diashow.startAnimation(anim);
            diashow_description.setVisibility(View.GONE);
        }
    }

    @Override
    public void nextImage() {
        Log.d(TAG, "nextImage: got activated");
        if (picture_names != null && picture_names.size() != 0) {
            if (diashow_position.incrementAndGet() > picture_names.size() - 1) {
                diashow_position.set(0);
            }
            downloadImage(picture_names.get(diashow_position.get()));
        }
    }

    @Override
    public void previousImage() {
        Log.d(TAG, "previousImage: listener got activated");
        if (picture_names != null && picture_names.size() != 0) {
            if (diashow_position.decrementAndGet() < 0) {
                diashow_position.set(picture_names.size() - 1);
            }
            downloadImage(picture_names.get(diashow_position.get()));
        }
    }

    @Override
    public void setProgressBar(int progress) {
        Log.d(TAG, "setProgressBar: " + progress);
        if (progress > 100) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress, true);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
