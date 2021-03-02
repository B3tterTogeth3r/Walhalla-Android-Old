package de.walhalla.app.fragments.donate;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.donate.ui.Buttons;

@SuppressLint("StaticFieldLeak")
public class Fragment extends CustomFragment {

    public static ClipboardManager clipboardManager;
    protected static Button aktive, hbv, kstv, hbv_donate;
    protected static View view;

    public Fragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donate, container, false);
        //TODO This design is awful.

        //Beim Anklicken der Felder wird die IBAN in die Zwischenablage kopiert
        Buttons.load();

        clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        aktive.setOnClickListener(new OnClick());

        hbv.setOnClickListener(new OnClick());

        kstv.setOnClickListener(new OnClick());

        hbv_donate.setOnClickListener(new OnClick());

        return view;
    }

    @Override
    public void onAuthChange() {
    }
}
