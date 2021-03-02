package de.walhalla.app.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.App;
import de.walhalla.app.R;

@SuppressLint("StaticFieldLeak")
public class ErrorDialog extends AlertDialog.Builder {
    private static final String TAG = "ErrorDialog";
    private final String whichOne;

    public ErrorDialog(Context context, String whichOne) {
        super(context);
        //Display an error message
        this.whichOne = whichOne;

        setTitle(R.string.error_title);
        setMessage(whichOne);
    }

    public static void display(FragmentManager fragmentManager, @NotNull String errorKind) {
        ErrorDialog editDialog = new ErrorDialog(App.getContext(), errorKind);
        editDialog.show();
    }
}
