package de.walhalla.app.fragments.addNew;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.interfaces.AddNewSemesterListener;

public class MessageDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ChargenDialog";
    private final AddNewSemesterListener addNewSemesterListener;

    public MessageDialog(AddNewSemesterListener addNewSemesterListener) {
        this.addNewSemesterListener = addNewSemesterListener;
    }

    public static void display(FragmentManager fragmentManager, ArrayList<Object> notes, AddNewSemesterListener addNewSemesterListener) {
        try {
            MessageDialog dialog = new MessageDialog(addNewSemesterListener);
            dialog.show(fragmentManager, TAG);
        } catch (Exception e) {
            Snackbar.make(MainActivity.parentLayout, R.string.error_display, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment, container, false);
        //TODO Initialize all the variables which have to respond with the ui
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do stuff
    }

    @Override
    public void onClick(View v) {

    }
}
