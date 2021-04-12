package de.walhalla.app.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.fragments.chargen.Fragment;
import de.walhalla.app.interfaces.ChangeChargeListener;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Chargen;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;

public class ChargenDialog extends DialogFragment implements View.OnClickListener,
        ChangeChargeListener, ChosenSemesterListener, DialogInterface.OnClickListener {
    public static final String[] CHARGEN_NAMES = new String[]{App.getContext().getString(R.string.charge_x),
            App.getContext().getString(R.string.charge_vx), App.getContext().getString(R.string.charge_fm),
            App.getContext().getString(R.string.charge_xx), App.getContext().getString(R.string.charge_xxx)};
    private static final String TAG = "ChargenDialog";
    int semID;
    private Chargen chargen = new Chargen(0, 0, 0, 0, 0, 0);
    private Chargen backupChargen = new Chargen(0, 0, 0, 0, 0, 0);
    private Button senior, consenior, fuxmajor, scriptor, kassier, semester_spinner;

    public ChargenDialog(int selected_semester) {
        this.semID = selected_semester;
    }

    public ChargenDialog() {
        this.semID = 0;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chargen, null);
        builder.setView(view);
        TextView title = view.findViewById(R.id.dialog_chargen_title);
        if (semID == 0) {
            title.setText(R.string.dialog_chargen_title);
        } else {
            title.setText(R.string.dialog_chargen_edit);
        }
        semester_spinner = view.findViewById(R.id.dialog_chargen_semester);
        senior = view.findViewById(R.id.dialog_chargen_senior);
        consenior = view.findViewById(R.id.dialog_chargen_consenior);
        fuxmajor = view.findViewById(R.id.dialog_chargen_fuxmajor);
        scriptor = view.findViewById(R.id.dialog_chargen_scriptor);
        kassier = view.findViewById(R.id.dialog_chargen_kassier);
        semester_spinner.setOnClickListener(this);
        senior.setOnClickListener(this);
        consenior.setOnClickListener(this);
        fuxmajor.setOnClickListener(this);
        scriptor.setOnClickListener(this);
        kassier.setOnClickListener(this);
        if (semID != 0) {
            final Semester semester = Variables.SEMESTER_ARRAY_LIST.get(semID);
            assert semester != null;
            backupChargen = new Chargen();//Find.Chargen(semester);
            try {
                chargen.setSemester(backupChargen.getSemester());
                chargen.setX(backupChargen.getX());
                chargen.setVX(backupChargen.getVX());
                chargen.setFM(backupChargen.getFM());
                chargen.setXX(backupChargen.getXX());
                chargen.setXXX(backupChargen.getXXX());
                semester_spinner.setText(semester.getLong());
                semester_spinner.setClickable(false);
                //TODO Find name
                /*senior.setText(Objects.requireNonNull(Find.Person(chargen.getX())).getFullName());
                consenior.setText(Objects.requireNonNull(Find.Person(chargen.getVX())).getFullName());
                fuxmajor.setText(Objects.requireNonNull(Find.Person(chargen.getFM())).getFullName());
                scriptor.setText(Objects.requireNonNull(Find.Person(chargen.getXX())).getFullName());
                kassier.setText(Objects.requireNonNull(Find.Person(chargen.getXXX())).getFullName());*/
            } catch (Exception ignored) {

            }
        }

        builder.setPositiveButton(R.string.send, this);
        builder.setNegativeButton(R.string.abort, this);

        return builder.create();
    }

    @Override
    public void onClick(@NotNull View v) {
        //TODO Fix that
        SinglePersonPickerDialog dialog = new SinglePersonPickerDialog(getContext(), this, -1);
        switch (v.getId()) {
            case (R.id.dialog_chargen_semester):
                ChangeSemesterDialog semDialog;
                if (semID == 0) {
                    semDialog = new ChangeSemesterDialog(this);
                    semDialog.show(getParentFragmentManager(), null);
                }
                break;
            case (R.id.dialog_chargen_senior):
                if (semID != 0) {
                    //dialog = new SinglePersonPickerDialog(getContext(), this, 0, chargen.getX());
                } else {
                    dialog = new SinglePersonPickerDialog(getContext(), this, 0);
                }
                dialog.show();
                break;
            case (R.id.dialog_chargen_consenior):
                if (semID != 0) {
                    //dialog = new SinglePersonPickerDialog(getContext(), this, 1, chargen.getVX());
                } else {
                    dialog = new SinglePersonPickerDialog(getContext(), this, 1);
                }
                dialog.show();
                break;
            case (R.id.dialog_chargen_fuxmajor):
                if (semID != 0) {
                    //dialog = new SinglePersonPickerDialog(getContext(), this, 2, chargen.getFM());
                } else {
                    dialog = new SinglePersonPickerDialog(getContext(), this, 2);
                }
                dialog.show();
                break;
            case (R.id.dialog_chargen_scriptor):
                if (semID != 0) {
                    //dialog = new SinglePersonPickerDialog(getContext(), this, 3, chargen.getXX());
                } else {
                    dialog = new SinglePersonPickerDialog(getContext(), this, 3);
                }
                dialog.show();
                break;
            case (R.id.dialog_chargen_kassier):
                if (semID != 0) {
                    //dialog = new SinglePersonPickerDialog(getContext(), this, 4, chargen.getXXX());
                } else {
                    dialog = new SinglePersonPickerDialog(getContext(), this, 4);
                }
                dialog.show();
                break;
            default:
                dismiss();
                break;
        }
    }

    @Override
    public void onDone(int Charge, int PersonID) {
        //Change it in the value of the Chargen chargen to the new PersonID
        Log.i(TAG, "TODO: send the person to the other dialog to the position of the " + Charge +
                "\n\tSelected personID is " + PersonID);
        switch (Charge) {
            case (0):
                chargen.setX(PersonID);
                //TODO senior.setText(Find.Person(PersonID).getFullName());
                break;
            case (1):
                chargen.setVX(PersonID);
                //TODO consenior.setText(Find.Person(PersonID).getFullName());
                break;
            case (2):
                chargen.setFM(PersonID);
                //TODO fuxmajor.setText(Find.Person(PersonID).getFullName());
                break;
            case (3):
                chargen.setXX(PersonID);
                //TODO scriptor.setText(Find.Person(PersonID).getFullName());
                break;
            case (4):
                chargen.setXXX(PersonID);
                //TODO kassier.setText(Find.Person(PersonID).getFullName());
                break;
            default:
                break;
        }
    }

    @Override
    public void changeSemesterDone() {
        chargen.setSemester(App.getChosenSemester().getID());
        semester_spinner.setText(App.getChosenSemester().getLong());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            //Check weather all the data is complete and possible and than upload it.
            if (chargen != backupChargen) {
                Variables.Firebase.Reference.CHARGEN.child(String.valueOf(chargen.getSemester())).child("x").setValue(chargen.getX());
                Variables.Firebase.Reference.CHARGEN.child(String.valueOf(chargen.getSemester())).child("vx").setValue(chargen.getVX());
                Variables.Firebase.Reference.CHARGEN.child(String.valueOf(chargen.getSemester())).child("fm").setValue(chargen.getFM());
                Variables.Firebase.Reference.CHARGEN.child(String.valueOf(chargen.getSemester())).child("xx").setValue(chargen.getXX());
                Variables.Firebase.Reference.CHARGEN.child(String.valueOf(chargen.getSemester())).child("xxx").setValue(chargen.getXXX())
                        .addOnFailureListener(e -> uploadError())
                        .addOnSuccessListener(aVoid -> dismiss());
            }
            dismiss();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            chargen = backupChargen;
            Toast.makeText(getContext(), "Abort", Toast.LENGTH_SHORT).show();
            setCancelable(true);
        }
    }

    private void uploadError() {
        Snackbar.make(requireView(), R.string.error_upload, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, v -> {
                })
                .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                .show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        androidx.fragment.app.Fragment fragment = new Fragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        super.onDismiss(dialog);
    }
}
