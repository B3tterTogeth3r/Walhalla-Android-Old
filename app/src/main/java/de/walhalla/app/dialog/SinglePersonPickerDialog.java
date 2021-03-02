package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.ChangeChargeListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Database;

public class SinglePersonPickerDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private final static String TAG = "SinglePersonPicker";
    private final int charge;
    private String personID;
    private final ArrayList<Person> arrayList;
    private final String[] names;
    private final ChangeChargeListener listener;

    public SinglePersonPickerDialog(Context context, ChangeChargeListener listener, int charge) {
        super(context);
        this.listener = listener;
        this.charge = charge;

        arrayList = Database.getPersonArrayList();
        names = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            names[i] = arrayList.get(i).getFullName();
        }
        setTitle(R.string.chargen_add);
        setSingleChoiceItems(names, -1, this);
        setPositiveButton(R.string.done, this);
    }

    public SinglePersonPickerDialog(Context context, ChangeChargeListener listener, int charge, String personID) {
        super(context);
        this.listener = listener;
        this.charge = charge;
        this.personID = personID;
        int checked = -1;

        arrayList = Database.getAktiveMembersArrayList();
        names = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            names[i] = arrayList.get(i).getFullName();
            if (arrayList.get(i).getId() == personID)
                checked = i;
        }
        setTitle(ChargenDialog.CHARGEN_NAMES[charge]);
        setSingleChoiceItems(names, checked, this);
        setPositiveButton(R.string.done, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //User clicked a person
        if (which == -1) {
            //Send the data back to ChargenDialog
            if (listener != null) {
                //TODO personID must be a string now; listener.onDone(charge, personID);
            }
        } else {
            //Change the data
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getFullName().equals(names[which])) {
                    personID = arrayList.get(i).getId();
                }
            }
        }
    }
}
