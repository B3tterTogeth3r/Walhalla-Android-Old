package de.walhalla.app.fragments.chargen_phil;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import de.walhalla.app.App;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.dialog.ChargenDialog;
import de.walhalla.app.interfaces.ChosenSemesterListener;

public class OnClick implements View.OnClickListener {
    private final View view;

    public OnClick(ImageButton view) {
        this.view = view;
    }

    public OnClick(Button view) {
        this.view = view;
    }

    @Override
    public void onClick(View v) {
        if (v == Fragment.add) {
            add();
        } else if (v == Fragment.edit) {
            edit();
        } else if (v == Fragment.title) {
            change();
        }
    }

    protected void add() {
        ChargenDialog dialog = new ChargenDialog();
        dialog.show(Fragment.f.getParentFragmentManager(), null);
    }

    protected void edit() {
        ChargenDialog dialog = new ChargenDialog(App.getChosenSemester().getID());
        dialog.show(Fragment.f.getParentFragmentManager(), null);
    }

    protected void change() {
        ChangeSemesterDialog changeSem = new ChangeSemesterDialog((ChosenSemesterListener) Fragment.f);
        changeSem.show(Fragment.f.getParentFragmentManager(), null);
    }

}
