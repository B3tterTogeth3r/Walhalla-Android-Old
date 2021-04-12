package de.walhalla.app.fragments.chargen;

import android.view.View;

import de.walhalla.app.dialog.ChangeSemesterDialog;

public class OnClick extends Fragment implements View.OnClickListener {
    protected final String TAG = "ProgramOnClick";
    protected Fragment f;

    public OnClick(Fragment fragment) {
        this.f = fragment;
    }

    @Override
    public void onClick(View v) {
        if (v == f.toolbar) {
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(f);
            changeSem.show(f.getParentFragmentManager(), null);
        }
    }
}
