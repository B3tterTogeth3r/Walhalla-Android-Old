package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.personSelectorListener;

public class MultiPersonPickerDialog extends AlertDialog.Builder implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {
    private final static String TAG = "MultiPersonPickerDialog";
    private final personSelectorListener listener;
    private final int whichJob;
    private final List<String> persons;
    private final List<String> selectedPersons;

    public MultiPersonPickerDialog(Context context, final int whichJob, List<String> selectedPersons, @NotNull List<String> persons, personSelectorListener listener) {
        super(context);
        this.listener = listener;
        this.whichJob = whichJob;
        this.persons = persons;
        this.selectedPersons = selectedPersons;

        String[] names = new String[persons.size()];
        boolean[] checkedItems = new boolean[persons.size()];

        for (int i = 0; i < persons.size(); i++) {
            names[i] = persons.get(i);
            checkedItems[i] = isInList(persons.get(i));
            if (checkedItems[i])
                this.selectedPersons.add(persons.get(i));
        }

        setTitle(JobPickerDialog.tasks[whichJob - 1]);
        setMultiChoiceItems(names, checkedItems, this);
        setPositiveButton(R.string.done, this);
    }

    public static void load(Context context, final int whichJob, List<String> selectedPersons, @NotNull List<String> persons, personSelectorListener listener) {
        try {
            MultiPersonPickerDialog dialog = new MultiPersonPickerDialog(context, whichJob, selectedPersons, persons, listener);
            dialog.show();
        } catch (Exception e) {
            Log.d(TAG, "MultiPersonPickerDialog:load:error", e);
        }
    }

    private boolean isInList(String personId) {
        for (int i = 0; i < selectedPersons.size(); i++) {
            if (personId.equals(selectedPersons.get(i)))
                return true;
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            // If the user checked the item, add it to the selected items
            selectedPersons.add(persons.get(which));
            Log.i(TAG, persons.get(which));
            //helperArrayList.add(new Helper(event.getId(), kind.getId(), person.get(which).getID()));
        } else if (selectedPersons.contains(persons.get(which))) {
            // Else, if the item is already in the array, remove it
            selectedPersons.remove(persons.get(which));
            Log.i(TAG, persons.get(which));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {  //User clicked on the positive button
            try {
                listener.onPersonSelectorDone(whichJob, selectedPersons);
            } catch (Exception e){
                Log.d(TAG, "listener:done:error", e);
            }
        }
    }
}
