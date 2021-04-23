package de.walhalla.app.fragments.addNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.interfaces.AddNewSemesterListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;

public class NewSemesterDialog extends DialogFragment implements View.OnClickListener, AddNewSemesterListener {
    private static final String TAG = "NewSemesterDialog";
    public static AddNewSemesterListener listener;
    public static Semester semester;
    public static ArrayList<Object> chargenList = new ArrayList<>();
    public static ArrayList<Object> philChargenList = new ArrayList<>();
    public static ArrayList<Object> eventsList = new ArrayList<>();
    public static ArrayList<Object> greetingList = new ArrayList<>();
    public static ArrayList<Object> notesList = new ArrayList<>();
    public static ArrayList<Object> message = new ArrayList<>();
    public static ArrayList<Drawable> imagesAktive = new ArrayList<>();
    public static ArrayList<Drawable> imagesAH = new ArrayList<>();
    private int downloadProgress = 0;
    private Button greeting, notes, chargen, phil_chargen, events, messageBT, send, semesterBT;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    public NewSemesterDialog() {
    }

    public static void display(FragmentManager fragment) {
        try {
            NewSemesterDialog dialog = new NewSemesterDialog();
            dialog.show(fragment, TAG);
        } catch (Exception e) {
            Snackbar.make(MainActivity.parentLayout, R.string.error_display, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_new_semester, container, false);
        greeting = view.findViewById(R.id.new_greeting);
        notes = view.findViewById(R.id.new_notes);
        chargen = view.findViewById(R.id.new_chargen);
        phil_chargen = view.findViewById(R.id.new_chargen_phil);
        events = view.findViewById(R.id.new_program);
        messageBT = view.findViewById(R.id.new_message);
        semesterBT = view.findViewById(R.id.new_semester);
        toolbar = view.findViewById(R.id.new_details_toolbar);
        progressBar = view.findViewById(R.id.progressBar2);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.attention)
                    .setMessage(R.string.abort_sure)
                    .setCancelable(true)
                    .setNeutralButton(R.string.abort, null)
                    .setPositiveButton(R.string.yes, ((dialog, which) -> this.dismiss()));
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });
        toolbar.setTitle(R.string.menu_new_semester);
        toolbar.setOnMenuItemClickListener(item -> false);
        progressBar.setProgress(downloadProgress);
        semesterBT.setClickable(true);
        semesterBT.setOnClickListener(this);
    }

    private void uploadAll() {
        Snackbar.make(MainActivity.parentLayout, R.string.toast_still_in_dev, Snackbar.LENGTH_LONG).show();
        //Upload Chargen

        int size = chargenList.size();
        for (int i = 0; i < size; i++) {
            //TODO Check if image got changed in the first place to avoid duplicate data
            //Set image_path if an image got selected
            if(imagesAktive.get(i) != null){
                String name = "Charge_" + i + "_sem_" + semester.getID() + ".jpg";
                //Upload image
                Bitmap bitmap = ((BitmapDrawable)imagesAktive.get(i)).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] data = baos.toByteArray();
                StorageReference imageRef = Variables.Firebase.IMAGES.child(name);
                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnFailureListener(e -> Log.d(TAG, "Image available: upload error", e))
                        .addOnSuccessListener(taskSnapshot -> Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getName())));
                ((Person)chargenList.get(i)).setPicture_path("/pictures/" + name);
            }
            Variables.Firebase.FIRESTORE.collection("Semester")
                    .document(String.valueOf(semester.getID()))
                    .collection("Chargen")
                    .document(String.valueOf(i))
                    .set(chargenList.get(i), SetOptions.merge());
        }
        for (int i = 0; i < size; i++) {
            //TODO Check if image got changed in the first place to avoid duplicate data
            //Set image_path if an image got selected
            if(imagesAH.get(i) != null){
                String name = "Phil_Charge_" + i + "_sem_" + semester.getID() + ".jpg";
                //Upload image
                Bitmap bitmap = ((BitmapDrawable)imagesAH.get(i)).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] data = baos.toByteArray();
                StorageReference imageRef = Variables.Firebase.IMAGES.child(name);
                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnFailureListener(e -> Log.d(TAG, "Image available: upload error", e))
                        .addOnSuccessListener(taskSnapshot -> Log.d(TAG, Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getName())));
                ((Person)philChargenList.get(i)).setPicture_path("/pictures/" + name);
            }
        }
        //Upload Phil Chargen
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .document("x")
                .set(philChargenList.get(0), SetOptions.merge());
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .document("xx")
                .set(philChargenList.get(1), SetOptions.merge());
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .document("xxx")
                .set(philChargenList.get(2), SetOptions.merge());
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .document("HW")
                .set(philChargenList.get(3), SetOptions.merge());
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .collection("Chargen_Phil")
                .document("hw")
                .set(philChargenList.get(4), SetOptions.merge());
        //Upload Greeting and notes
        Semester sem = semester;
        sem.setGreeting(greetingList);
        sem.setNotes(notesList);
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(String.valueOf(semester.getID()))
                .set(sem, SetOptions.merge());
    }

    @Override
    public void onClick(View v) {
        if (greeting.equals(v)) {
            //Add a new greeting
            GreetingDialog.display(getChildFragmentManager(), greetingList, this);
        } else if (semesterBT.equals(v)) {
            Log.d(TAG, "choose the semester.");
            ChangeSemesterDialog.load(getChildFragmentManager(), this);
        } else if (notes.equals(v)) {
            Log.d(TAG, "Add or copy some new notes.");
            NotesDialog.display(getChildFragmentManager(), notesList, this);
        } else if (chargen.equals(v)) {
            Log.d(TAG, "Add new chargen for active members.");
            ChargenDialog.display(getChildFragmentManager(), ChargenDialog.kinds[0], chargenList, this);
        } else if (phil_chargen.equals(v)) {
            Log.d(TAG, "Add or copy phil chargen.");
            ChargenDialog.display(getChildFragmentManager(), ChargenDialog.kinds[1], philChargenList, this);
        } else if (events.equals(v)) {
            Log.d(TAG, "Add new (multiple) events.");
            Snackbar.make(requireView(), R.string.toast_still_in_dev, Snackbar.LENGTH_SHORT).show();
            //EventsDialog.display(getChildFragmentManager(), null, this);
        } else if (messageBT.equals(v)) {
            Log.d(TAG, "Add a new Messages with the link to the pdf on the website.");
            Snackbar.make(requireView(), R.string.toast_still_in_dev, Snackbar.LENGTH_SHORT).show();
            //MessageDialog.display(getChildFragmentManager(), message, this);
        } else {
            Snackbar.make(requireView(), R.string.error_site_messages, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void semesterDone(@NotNull Semester semester) {
        Log.d(TAG, "The chosen semester is the " + semester.getLong());
        NewSemesterDialog.semester = semester;
        //TODO Write the selected semester somewhere
        //Get available data, if there is any for that semester.
        TableLayout layout = requireView().findViewById(R.id.new_semester_table_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
        }
        downloadProgress = 0;
        progressBar.setProgress(downloadProgress);
        progressBar.setVisibility(View.VISIBLE);
        int semester_id = semester.getID();
        System.out.println(semester_id);
        try {
            Log.d(TAG, "start Chargen");
            Variables.Firebase.FIRESTORE
                    .collection("Semester")
                    .document(String.valueOf(semester_id))
                    .collection("Chargen")
                    //.limit(5)
                    .get()
                    .addOnFailureListener(e -> Log.e(TAG, "an error occurred", e))
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        NewSemesterDialog.chargenList = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                try {
                                    NewSemesterDialog.chargenList.add(snapshot.toObject(Person.class));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                        downloadProgress = downloadProgress + 25;
                        progressBar.setProgress(downloadProgress);
                        Log.d(TAG, downloadProgress + "; chargen done");
                        downloadCompleteListener();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "start Phil chargen");
        Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester_id))
                .collection("Chargen_Phil")
                .limit(5)
                .orderBy("id")
                .get()
                .addOnFailureListener(e -> Log.e(TAG, "an error occurred", e))
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    NewSemesterDialog.philChargenList = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            try {
                                NewSemesterDialog.philChargenList.add(snapshot.toObject(Person.class));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    downloadProgress = downloadProgress + 25;
                    progressBar.setProgress(downloadProgress);
                    Log.d(TAG, downloadProgress + "; Phil chargen done");
                    downloadCompleteListener();
                });

        Log.d(TAG, "Start Greeting");
        Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester_id))
                .get()
                .addOnFailureListener(e -> Log.e(TAG, "an error occurred", e))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            NewSemesterDialog.greetingList.clear();
                            NewSemesterDialog.greetingList = (ArrayList<Object>) documentSnapshot.get("greeting");
                        } catch (Exception ignored) {
                        }
                    }
                    downloadProgress = downloadProgress + 25;
                    progressBar.setProgress(downloadProgress);
                    Log.d(TAG, downloadProgress + "; Greeting done");
                    downloadCompleteListener();
                });

        Log.d(TAG, "Start notes");
        Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(semester_id))
                .get()
                .addOnFailureListener(e -> Log.e(TAG, "an error occurred", e))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            NewSemesterDialog.notesList = (ArrayList<Object>) documentSnapshot.get("notes");
                        } catch (Exception ignored) {
                        }
                    }
                    downloadProgress = downloadProgress + 25;
                    progressBar.setProgress(downloadProgress);
                    Log.d(TAG, downloadProgress + "; notes done");
                    downloadCompleteListener();
                });
    }

    private void downloadCompleteListener() {
        if (downloadProgress == 100) {
            TableLayout layout = requireView().findViewById(R.id.new_semester_table_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(true);
            }
            progressBar.setVisibility(View.GONE);
            //Activate own button
            chargen.setClickable(true);
            chargen.setOnClickListener(this);

            //Start chargen
            ChargenDialog.display(getChildFragmentManager(), ChargenDialog.kinds[0], chargenList, this);
        }
    }

    @Override
    public void chargenDone(@NotNull ArrayList<Object> chargenList, ArrayList<Drawable> allImages) {
        System.out.println(progressBar.getProgress());
        //Activate next button
        phil_chargen.setClickable(true);
        phil_chargen.setOnClickListener(this);
        Log.d(TAG, chargenList.size() + "");
        NewSemesterDialog.chargenList = chargenList;
        NewSemesterDialog.imagesAktive = allImages;
        //Open Phil Chargen Dialog
        ChargenDialog.display(getChildFragmentManager(), ChargenDialog.kinds[1], philChargenList, this);
    }

    @Override
    public void philChargenDone(@NotNull ArrayList<Object> philChargenList, ArrayList<Drawable> allImages) {
        //Activate next button
        greeting.setClickable(true);
        greeting.setOnClickListener(this);
        NewSemesterDialog.philChargenList = philChargenList;
        NewSemesterDialog.imagesAH = allImages;

        //Open Greeting site
        GreetingDialog.display(getChildFragmentManager(), greetingList, this);
    }

    /**
     * Disabled right now because it is easier to add new events in the program fragment
     *
     * @param eventsList list of events to upload on save.
     */
    @Override
    public void eventsDone(@NotNull ArrayList<Event> eventsList) {
        //Activate next button
        //events.setClickable(true);
        //events.setOnClickListener(this);

        //Open events dialog
        //EventsDialog.display(getChildFragmentManager(), null, this);
    }

    @Override
    public void greetingDone(@NotNull ArrayList<Object> greetingList) {
        //Activate next button
        notes.setClickable(true);
        notes.setOnClickListener(this);
        //Add last line and current x and ahx
        String lastLine = getString(R.string.greeting_end);
        greetingList.add(lastLine);
        Map<String, String> sign = new HashMap<>();
        sign.put("Aktivensenior", ((Person) chargenList.get(0)).getFullName());
        sign.put("Philistersenior", ((Person) philChargenList.get(0)).getFullName());
        greetingList.add(sign);
        NewSemesterDialog.greetingList = greetingList;

        //Open notes dialog
        NotesDialog.display(getChildFragmentManager(), notesList, this);
    }

    @Override
    public void notesDone(@NotNull ArrayList<Object> notesList) {
        //Activate next button
        messageBT.setClickable(true);
        messageBT.setOnClickListener(this);
        NewSemesterDialog.notesList = notesList;

        //Activate button to publish the whole new input
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send) {
                Log.d(TAG, "send");
                uploadAll();
                this.dismiss();
            }
            return true;
        });
    }

    @Override
    public void messageDone(@Nullable ArrayList<Object> message) {
        //Open message dialog
        //MessageDialog.display(getChildFragmentManager(), message, this);
    }
}
