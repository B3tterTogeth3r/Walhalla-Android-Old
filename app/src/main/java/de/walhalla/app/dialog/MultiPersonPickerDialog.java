package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.personSelectorListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.models.HelperKind;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Database;

public class MultiPersonPickerDialog extends AlertDialog.Builder implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {
    private final static String TAG = "MultiPersonPickerDialog";
    private final HelperKind kind;
    private final ArrayList<Helper> helperArrayList;
    private final Event event;
    private final ArrayList<Integer> selectedItems;
    private final ArrayList<Person> person;
    private final personSelectorListener listener;

    public MultiPersonPickerDialog(Context context, final Event event, final HelperKind kind,
                                   final ArrayList<Helper> helperArrayList,
                                   personSelectorListener listener) {
        super(context);
        this.event = event;
        this.kind = kind;
        this.helperArrayList = helperArrayList;
        this.listener = listener;

        person = Database.getAktiveMembersArrayList();
        selectedItems = new ArrayList<>();
        String[] names = new String[person.size()];
        boolean[] checkedItems = new boolean[person.size()];

        for (int i = 0; i < person.size(); i++) {
            names[i] = person.get(i).getFullName();
            checkedItems[i] = isInList(person.get(i).getId(), helperArrayList);
            if (checkedItems[i])
                selectedItems.add(i);
        }

        setTitle(kind.getTag());
        setMultiChoiceItems(names, checkedItems, this);
        setPositiveButton(R.string.done, this);
    }

    private boolean isInList(String personId, @NotNull ArrayList<Helper> helperArrayList) {
        for (int i = 0; i < helperArrayList.size(); i++) {
            if (String.valueOf(personId).equals(helperArrayList.get(i).getPerson()))
                return true;
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            // If the user checked the item, add it to the selected items
            selectedItems.add(which);
            Log.i(TAG, person.get(which).getFullName());
            //helperArrayList.add(new Helper(event.getId(), kind.getId(), person.get(which).getID()));

        } else if (selectedItems.contains(which)) {
            // Else, if the item is already in the array, remove it
            selectedItems.remove(Integer.valueOf(which));
            Log.i(TAG, person.get(which).getFullName());
            int i = 0;
            int size = helperArrayList.size();
            do {
                Helper h = helperArrayList.get(i);
                if (person.get(which).getId().equals(h.getPerson())) {
                    helperArrayList.remove(h);
                    break;
                }
                i++;
            } while (i < size);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {  //User clicked on the positive button
            listener.onPersonSelectorDone(event, helperArrayList, kind);
        }
    }
}
