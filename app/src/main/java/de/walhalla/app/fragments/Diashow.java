package de.walhalla.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import de.walhalla.app.R;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.interfaces.StopDiashowListener;
import de.walhalla.app.utils.Variables;

public class Diashow implements View.OnClickListener, StopDiashowListener {
    private static final String TAG = "Diashow";
    public static StopDiashowListener listener;
    private final Context context;
    private final Animation anim, anim_out;
    private final AtomicInteger diashow_position = new AtomicInteger(0);
    private final AtomicBoolean threadAlive = new AtomicBoolean(false);
    private final ArrayList<Thread> threads = new ArrayList<>();
    private RelativeLayout layout;
    private TextView diashow_description;
    private ImageView diashow;
    private ImageButton diashow_right, diashow_left;
    private ArrayList<String> picture_names;
    private String threadName;

    public Diashow(Context context) {
        this.context = context;
        listener = this;
        anim = AnimationUtils.loadAnimation(context, R.anim.picture_change);
        anim_out = AnimationUtils.loadAnimation(context, R.anim.picture_change_out);
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
                    changeDisplayedImage();

                    if (picture_names.size() == 1) {
                        ButtonVisibility(false);
                    } else {
                        ButtonVisibility(true);
                        //Automatic switch between images after 3-5 seconds.
                        startSwitchTimer();
                        //TODO Activate swipe gestures
                    }
                }
            }
        });
    }

    /**
     * Automatic switch between images after 3-5 seconds.
     */
    private void startSwitchTimer() {
        Random random = new Random();
        int randomNumber = random.nextInt(4000);
        Thread thread = new Thread(() -> {
            try {
                while (threadAlive.get()) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(6000 + randomNumber);
                    } catch (Exception ignored) {
                    }
                    if (threadAlive.get()) {
                        if (picture_names != null && picture_names.size() != 0) {
                            if (diashow_position.incrementAndGet() > picture_names.size() - 1) {
                                diashow_position.set(0);
                            }
                            changeDisplayedImage();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });

        threadAlive.set(true);
        thread.setName(threadName);
        threads.add(thread);
        //TODO How to stop multiple diashow elements inside one fragment? While there is a problem, no automatic diashow should start.
        //thread.start();
    }

    private void ButtonVisibility(boolean Visible) {
        int visibility = View.GONE;
        if (Visible) {
            visibility = View.VISIBLE;
        }
        diashow_right.setVisibility(visibility);
        diashow_left.setVisibility(visibility);
    }

    @Override
    public void onClick(@NotNull View v) {
        //Diashow button left got clicked
        if (v.getId() == diashow_left.getId()) {
            Log.d(TAG, "Button left got clicked");
            if (picture_names != null && picture_names.size() != 0) {
                if (diashow_position.decrementAndGet() < 0) {
                    diashow_position.set(picture_names.size() - 1);
                }
                changeDisplayedImage();
            }
        }
        //Diashow button right got clicked
        else if (v.getId() == diashow_right.getId()) {
            Log.d(TAG, "Button right got clicked");
            if (picture_names != null && picture_names.size() != 0) {
                if (diashow_position.incrementAndGet() > picture_names.size() - 1) {
                    diashow_position.set(0);
                }
                changeDisplayedImage();
            }
        }
    }

    private void changeDisplayedImage() {
        try {
            downloadImage(picture_names.get(diashow_position.get()));
        } catch (Exception e) {
            Log.e(TAG, "downloadImage(picture_names.get(diashow_position.get())) created an exception");
            //e.printStackTrace();
        }
    }

    private void downloadImage(String image_name) {
        diashow_right.setClickable(false);
        diashow_left.setClickable(false);
        diashow.startAnimation(anim_out);

        StorageReference image = FirebaseStorage.getInstance().getReference(image_name);
        image.getBytes(Variables.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            //Animate a blend from one to the next image
            try {
                diashow.startAnimation(anim);
            } catch (Exception ignored) {
            }
            diashow.setImageBitmap(Firebase.addWatermark(bmp));
            diashow_right.setClickable(true);
            diashow_left.setClickable(true);
            downloadName(image_name);
        }).addOnFailureListener(e ->
                Log.e(TAG, "download of image " + image_name + " unsuccessful", e));
    }

    private void downloadName(String image_name) {
        Variables.Firebase.FIRESTORE.collection("Data")
                .whereEqualTo("name", image_name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            diashow_description.setText((String) documentSnapshot.get("title"));
                        }
                    } else {
                        diashow_description.setText("");
                    }
                })
                .addOnFailureListener(e -> diashow_description.setText(image_name));
    }

    @Override
    public void stopDiashow() {
        //Stop the automatic new images
        for (Thread thread : threads) {
            if (thread.isAlive() | threadAlive.get()) {
                threadAlive.set(false);
                thread.interrupt();
                if (thread.isInterrupted()) {
                    Log.d(TAG, "Thread successfully interrupted " + thread.getName());
                }
            }
            threads.remove(thread);
        }
    }
}
