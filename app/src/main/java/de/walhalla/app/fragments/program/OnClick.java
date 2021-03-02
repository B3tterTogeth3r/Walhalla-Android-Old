package de.walhalla.app.fragments.program;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.fragments.program.ui.Edit;
import de.walhalla.app.models.Event;

public class OnClick implements View.OnClickListener {
    protected final String TAG = "ProgramOnClick";
    protected final Fragment f = Fragment.f;
    protected View view;
    protected Event event = new Event(), toUpload = new Event();

    public OnClick(Button button) {
        this.view = button;
    }

    public OnClick(ImageButton button) {
        this.view = button;
    }

    @Override
    public void onClick(View v) {
        if (v == Fragment.chooseSemester) {
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(f);
            changeSem.show(f.getParentFragmentManager(), null);
        } else if (v == Fragment.add) {
            Edit.display(f.getParentFragmentManager(), null);
        }
    }
}
