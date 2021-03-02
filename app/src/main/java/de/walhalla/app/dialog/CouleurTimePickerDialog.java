package de.walhalla.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.CouleurTimePickerListener;

public class CouleurTimePickerDialog extends Dialog {
    String TAG = "ChangeSemDialog";
    CouleurTimePickerListener listener;

    public CouleurTimePickerDialog(@NonNull Context context, CouleurTimePickerListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Change Semester");
        setContentView(R.layout.dialog_number_couleur_time);
        setCanceledOnTouchOutside(false);
        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        /* Select collar kind **/
        final NumberPicker punct = findViewById(R.id.numberPicker1);
        final String[] semester = new String[]{"ct", "st"};
        punct.setDisplayedValues(semester);
        punct.setMaxValue(1);
        punct.setMinValue(0);
        punct.setWrapSelectorWheel(false);
        punct.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        /* Select relativity to other events **/
        final NumberPicker np2 = findViewById(R.id.numberPicker3);
        final String[] dayTime = new String[]{"", "anschließend", "ganztägig", "gleicher Tag", "Info", "noch unbekannt"};
        np2.setDisplayedValues(dayTime);
        np2.setMinValue(0);
        np2.setMaxValue(4);
        np2.setWrapSelectorWheel(false);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        /* Select relativity to other events **/
        final NumberPicker np3 = findViewById(R.id.numberPicker2);
        final String[] collar = new String[]{"io", "o", "ho"};
        np3.setDisplayedValues(collar);
        np3.setMaxValue(2);
        np3.setMinValue(0);
        np3.setWrapSelectorWheel(true);
        np3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, semester[punct.getValue()]);
                Log.i(TAG, dayTime[np2.getValue()]);
                Log.i(TAG, collar[np3.getValue()]);
                listener.notifyOfDialogDone(semester[punct.getValue()], dayTime[np2.getValue()], collar[np3.getValue()]);
                dismiss();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}