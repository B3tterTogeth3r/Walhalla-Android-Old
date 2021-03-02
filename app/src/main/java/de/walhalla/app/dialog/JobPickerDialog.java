package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.R;
import de.walhalla.app.fragments.program.Fragment;
import de.walhalla.app.interfaces.RunnableCompleteListener;
import de.walhalla.app.interfaces.UploadListener;
import de.walhalla.app.interfaces.personSelectorListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.models.HelperKind;
import de.walhalla.app.threads.NotifyingRunnable;
import de.walhalla.app.utils.Database;

public class JobPickerDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
        personSelectorListener, UploadListener, RunnableCompleteListener {
    private final static String TAG = "JobPickerDialog";
    private final Event event;
    private final ArrayList<HelperKind> helperKinds;
    private int i = 0;
    private DialogInterface dialog;

    public JobPickerDialog(Context context, Event event) {
        super(context);
        this.event = event;
        helperKinds = Database.getHelperKindArrayList();

        //Show them on the click of a button in a selector
        CharSequence[] charSequence;
        charSequence = new String[helperKinds.size()];
        for (int index = 0; index < helperKinds.size(); index++) {
            charSequence[index] = helperKinds.get(index).getTag();
        }

        setTitle(event.getTitle());
        setItems(charSequence, this);
        setPositiveButton(R.string.done, this);
        setNegativeButton(R.string.abort, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.dialog = dialog;
        if (which == -1) { // User clicked on the positive button
            Log.i(TAG, "Positive button clicked.");
            //Remove everything from the database table belonging to that event.
            //TODO Upload them to the firebase realtime database
            //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, new Helper(event.getId(), 0, 0), Variables.DELETE);
        } else if (which == -2) { // User clicked on the negative button
            //Fragment.allhelpers = Find.help4event(event);
            Log.i(TAG, "Negative button clicked.");
            dialog.dismiss();
        } else { //User selected a task
            /*MultiPersonPickerDialog personPickerDialog = new MultiPersonPickerDialog(getContext(),
                    event, helperKinds.get(which), Find.helpOfKind(event, helperKinds.get(which)),
                    this);
            personPickerDialog.show();*/
        }
    }

    @Override
    public void onDatabaseUploadDone(boolean successful) {
        //Upload the new data
        if (successful) {
            if (i < Fragment.allhelpers.size()) {
                //TODO Upload them to the firebase realtime database
                //UploadToDatabaseRunnable databaseRunnable = new UploadToDatabaseRunnable(this, Fragment.allhelpers.get(i), Variables.ADD);
                i++;
            } else {
                //TODO Create upload to firebase realtime database
            }
        }
    }

    @Override
    public void notifyOfRunnableComplete(NotifyingRunnable runnable) {
        dialog.dismiss();
    }

    @Override
    public void onPersonSelectorDone(Event event, @NotNull ArrayList<Helper> helperArrayList, HelperKind kind) {
        //Remove all entries from the array where event and kind are the same as the new array
        Fragment.allhelpers.removeIf(n -> (n.getKind() == kind.getId()));//TODO && n.getEvent() == event.getId()));
        Fragment.allhelpers.addAll(helperArrayList);
        //Reopen dialog
        JobPickerDialog dialog = new JobPickerDialog(getContext(), event);
        dialog.show();
    }
}
