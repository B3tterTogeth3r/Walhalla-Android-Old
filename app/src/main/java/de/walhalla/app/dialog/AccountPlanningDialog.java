package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.List;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.NumberPickerCompleteListener;

public
class AccountPlanningDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
    private static final String TAG = "AccountPlanningDialog";
    private final NumberPicker np;
    private final String[] pickerData;
    private NumberPickerCompleteListener listener;

    public AccountPlanningDialog(Context context, List<String> names, NumberPickerCompleteListener listener) {
        super(context);
        if (listener != null) {
            this.listener = listener;
        } else {
            Log.e(TAG, "Listener not initialised!");
        }
        pickerData = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            pickerData[i] = names.get(i);
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View myView = layoutInflater.inflate(R.layout.dialog_number, null);
        setView(myView);
        np = myView.findViewById(R.id.numberPicker1);
        np.setMaxValue(pickerData.length - 1);
        np.setDisplayedValues(pickerData);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        setPositiveButton(R.string.send, this);
        setNegativeButton(R.string.abort, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case (DialogInterface.BUTTON_POSITIVE):
                Log.d(TAG, "Positive button got pressed");
                try {
                    listener.notifyOfAccountingDone((String) pickerData[np.getValue()]);
                } catch (Exception e) {
                    Log.d(TAG, "Listener not initialised", e);
                }
                break;
            case (DialogInterface.BUTTON_NEGATIVE):
                Log.d(TAG, "Negative button got pressed");
                break;
            default:
                dialog.dismiss();
                break;
        }
    }
}
