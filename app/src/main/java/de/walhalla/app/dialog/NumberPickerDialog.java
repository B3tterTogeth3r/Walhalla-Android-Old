package de.walhalla.app.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.NumberpickerCompleteListener;
import de.walhalla.app.models.AllEvents;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Database;

public class NumberPickerDialog extends android.app.AlertDialog.Builder implements DialogInterface.OnClickListener {
    String TAG = "ChangeSemDialog";
    private ArrayList<Semester> semesterArrayList = null;
    private ArrayList<AllEvents> allEvents = null;
    private ArrayList<Event> Events = null;
    private String[] pickerData;
    private NumberpickerCompleteListener listener;
    private final NumberPicker np;

    public NumberPickerDialog(Context context, @Nullable String differentKind, @Nullable NumberpickerCompleteListener listener) {
        super(context);
        if (differentKind == null) {
            semesterArrayList = Database.getSemesterArrayList();
            pickerData = new String[semesterArrayList.size()];
        } else if (differentKind.equals("ProgramDialog")) {
            allEvents = Database.getAllEventsArrayList();
            pickerData = new String[allEvents.size()];
        } else if (differentKind.equals("EventsDialog")) {
            Events = new ArrayList<>();//TODO Find.SemesterEvents(App.getChosenSemester());
            pickerData = new String[Events.size()];
        }
        if (listener != null) {
            this.listener = listener;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View myView = layoutInflater.inflate(R.layout.dialog_number, null);
        setView(myView);

        np = myView.findViewById(R.id.numberPicker1);
        if (semesterArrayList != null) {
            /* Select Semester **/
            for (int i = 0; semesterArrayList.size() > i; i++) {
                pickerData[i] = semesterArrayList.get(i).getLong();
            }
            int startValue = App.getCurrentSemester().getID() - 1;
            np.setValue(startValue);
            np.setWrapSelectorWheel(false);
        } else if (allEvents != null) {
            for (int i = 0; allEvents.size() > i; i++) {
                pickerData[i] = allEvents.get(i).getName();
            }
            np.setWrapSelectorWheel(true);
        } else if (Events != null) {
            for (int i = 0; Events.size() > i; i++) {
                pickerData[i] = Events.get(i).getTitle();
            }
            np.setWrapSelectorWheel(true);
        }
        np.setMaxValue(pickerData.length - 1);
        np.setDisplayedValues(pickerData);
        np.setMinValue(0);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        setPositiveButton(R.string.send, this);
        setNegativeButton(R.string.abort, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case (DialogInterface.BUTTON_POSITIVE):
                if (semesterArrayList != null) {
                    Semester chosenOne = semesterArrayList.get(np.getValue());
                    Log.i(TAG, chosenOne.getLong());
                    App.setChosenSemester(chosenOne);
                } else if (allEvents != null) {
                    //return the selected value to the ProgramDialog and set it into the Button
                    if (listener != null) {
                        listener.notifyOfNumberPickerDone(allEvents.get(np.getValue()));
                    }
                } else if (Events != null) {
                    //return the selected value to the ProgramDialog and set it into the Button
                    if (listener != null) {
                        listener.notifyOfNumberPickerDone(Events.get(np.getValue()));
                    }
                }
                break;
            default:
            case (DialogInterface.BUTTON_NEGATIVE):
                dialog.dismiss();
                break;
        }
    }
}