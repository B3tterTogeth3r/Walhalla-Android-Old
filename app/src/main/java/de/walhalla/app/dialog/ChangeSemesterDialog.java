package de.walhalla.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;

public class ChangeSemesterDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private final String TAG = "ChangeSemDialog";
    private NumberPicker np;
    private final ChosenSemesterListener listener;
    int startingID = -1;

    public ChangeSemesterDialog(ChosenSemesterListener listener) {
        this.listener = listener;
    }

    public ChangeSemesterDialog(ChosenSemesterListener listener, int startingID) {
        this.listener = listener;
        this.startingID = startingID - 1;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number, null);
        builder.setTitle(R.string.change_semester);
        builder.setView(view);
        np = view.findViewById(R.id.numberPicker1);
        /* Select Semester **/
        final ArrayList<Semester> semesterArrayList = Variables.SEMESTER_ARRAY_LIST;
        String[] semester = new String[semesterArrayList.size()];
        for (int i = 0; i < semesterArrayList.size(); i++) {
            semester[i] = semesterArrayList.get(i).getLong();
        }
        np.setDisplayedValues(semester);
        np.setMaxValue(App.getCurrentSemester().getID()); // Only one semester into the future available
        if (startingID != -1) {
            np.setValue(startingID);
        } else {
            np.setValue(App.getChosenSemester().getID() - 1);
        }
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        builder.setPositiveButton(R.string.send, this)
                .setNegativeButton(R.string.abort, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Semester chosenOne = Variables.SEMESTER_ARRAY_LIST.get(np.getValue());
            if (listener != null && startingID == -1) {
                listener.start(chosenOne);
            } else if (listener != null) {
                listener.joinedSemesterDone(chosenOne);
            }
            dismiss();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            dismiss();
        }
    }
}