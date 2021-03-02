package de.walhalla.app.fragments.donate.ui;

import de.walhalla.app.R;
import de.walhalla.app.fragments.donate.Fragment;

public class Buttons extends Fragment {

    public Buttons() {
    }

    public static void load() {
        aktive = view.findViewById(R.id.konto_aktive);
        hbv = view.findViewById(R.id.konto_hbv);
        kstv = view.findViewById(R.id.konto_kstv);
        hbv_donate = view.findViewById(R.id.konto_hbd_donate);
    }
}
