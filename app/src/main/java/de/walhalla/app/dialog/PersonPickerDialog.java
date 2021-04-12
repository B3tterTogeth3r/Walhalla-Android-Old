package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.fragments.addNew.ChargenDialog;

public class PersonPickerDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private final static String TAG = "PersonPickerDialog";
    private final String kind;

    public PersonPickerDialog(@NotNull Context context, @NotNull ArrayList<String> persons, @Nullable String kind) {
        super(context);
        this.kind = kind;
        int size = persons.size();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = persons.get(i);
        }
        setTitle(R.string.drink_select_person);
        setItems(names, this);
        if (kind != null) {
            setPositiveButton(R.string.add, this);
            setNeutralButton(R.string.no_charge, this);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            ChargenDialog.listener.dismissSelector(kind, which);
        } catch (Exception e) {
            Log.d(TAG, "Error:", e);
        }
    }

    public interface CustomDismissListener {
        void dismissSelector(@NotNull String kind, int position);
    }
}
