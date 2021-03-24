package de.walhalla.app.fragments.program;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import de.walhalla.app.dialog.ChangeSemesterDialog;

public class OnClick implements View.OnClickListener {
    protected final String TAG = "ProgramOnClick";
    protected Fragment f = Fragment.f;
    protected View view;
    protected String title;

    public OnClick(Button button) {
        this.view = button;
    }

    public OnClick(ImageButton button) {
        this.view = button;
    }

    public OnClick(String title){
        this.title = title;
        this.view = null;
    }

    public OnClick(Fragment fragment){
        this.f = fragment;
    }

    @Override
    public void onClick(View v) {
        if(v == Fragment.toolbar){
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(f);
            changeSem.show(f.getParentFragmentManager(), null);
        }
    }
}
