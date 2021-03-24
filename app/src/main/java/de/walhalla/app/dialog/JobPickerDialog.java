package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.NumberPickerCompleteListener;
import de.walhalla.app.interfaces.personSelectorListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

public class JobPickerDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
        personSelectorListener {
    private final static String TAG = "JobPickerDialog";
    protected static String[] tasks;
    private final NumberPickerCompleteListener listener;
    private final Map<String, Object> help;
    private final Map<String, Object> worker;
    private final Context context;

    public JobPickerDialog(Context context, @Nullable Map<String, Object> worker, @NotNull Map<String, Object> help, @NotNull NumberPickerCompleteListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.help = help;
        if(worker == null){
            this.worker = new HashMap<>();
        } else{
            this.worker = worker;
        }

        List<String> sortedKeysStr = new ArrayList<>(this.help.keySet());
        List<Integer> sortedKeys = new ArrayList<>();
        for (int l = 0; l < sortedKeysStr.size(); l++) {
            sortedKeys.add(Integer.parseInt(sortedKeysStr.get(l)));
        }
        Collections.sort(sortedKeys);
        int size = sortedKeys.size();
        tasks = new String[size];
        for (int i = 0; i < size; i++) {
            try {
                tasks[i] = (String) help.get(String.valueOf(sortedKeys.get(i)));
            } catch (Exception e) {
                Log.d(TAG, "tasks:cast:error", e);
            }
        }

        setTitle(R.string.tasks);
        setItems(tasks, this);
        setPositiveButton(R.string.done, this);
        setNegativeButton(R.string.abort, this);
    }

    public static void load(Context context, @NotNull Map<String, Object> worker, @NotNull Map<String, Object> help, NumberPickerCompleteListener listener) {
        try {
            JobPickerDialog dialog = new JobPickerDialog(context, worker, help, listener);
            dialog.show();
        } catch (Exception e) {
            Log.d(TAG, "Builder:error", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) { // User clicked on the positive button
            Log.i(TAG, "Positive button clicked.");
            if (listener != null && worker != null) {
                listener.notifyOfTaskDone(worker);
            }
        } else if (which == -2) { // User clicked on the negative button
            Log.i(TAG, "Negative button clicked.");
            dialog.dismiss();
        } else { //User selected a task
            Log.d(TAG, "user selected item #" + which + 1);
            //Load list of all active persons
            Variables.Firebase.FIRESTORE
                    .collection("Person")
                    .whereEqualTo("rank", "Aktiver")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<String> names = new ArrayList<>();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                try {
                                    Person p = snapshot.toObject(Person.class);
                                    names.add(Objects.requireNonNull(p).getFullName());
                                } catch (Exception e) {
                                    Log.d(TAG, "ParsingPerson: error @ " + (String) snapshot.get(Person.FIRST_NAME), e);
                                }
                            }
                            List<String> selected = new ArrayList<>();
                            try {
                                selected = (List<String>) help.get(String.valueOf(which + 1));
                            } catch (Exception ignored) {
                            }
                            MultiPersonPickerDialog.load(getContext(), which + 1, selected, names, JobPickerDialog.this);
                        }
                    });
        }
    }

    @Override
    public void onPersonSelectorDone(int which, @NotNull List<String> names) {
        Log.d(TAG, String.valueOf(which));
        int size = names.size();
        String[] selectedPersons = new String[size];
        for (int i = 0; i < size; i++) {
            selectedPersons[i] = names.get(i);
        }
        if(worker.containsKey(String.valueOf(which))) {
            System.out.println("if");
            worker.replace(String.valueOf(which), names);
        } else {
            System.out.println("else");
            worker.put(String.valueOf(which), names);
        }
        Log.d(TAG, "worker: size = " + worker.size());
        //Reopen dialog
        JobPickerDialog.load(context, worker, help, listener);
    }
}
