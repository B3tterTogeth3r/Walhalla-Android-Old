package de.walhalla.app.fragments.drink;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.drink.ui.Content;
import de.walhalla.app.fragments.drink.ui.Overview;
import de.walhalla.app.fragments.drink.ui.Title;

@SuppressWarnings("StaticFieldLeak")
public class Fragment extends CustomFragment {
    protected static Fragment f;
    protected static final float scale = App.getContext().getResources()
            .getDisplayMetrics().density;
    protected static final int defaultMargin = (int) scale * 15;
    protected static ImageButton price, invoice, payment;
    protected static TableLayout.LayoutParams params = new TableLayout.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT, //width
            TableRow.LayoutParams.WRAP_CONTENT, //height
            1f);

    public Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Fragment.f = this;
        ((Toolbar)requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.menu_beer);
        ((Toolbar)requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        LinearLayout layout = view.findViewById(R.id.fragment_container);
        layout.removeAllViewsInLayout();

        layout.addView(Title.load());
        layout.addView(Overview.load());
        layout.addView(Content.load());

        initialize();

        return view;
    }

    private void initialize() {
        try {
            price.setOnClickListener(new OnClick());
        } catch (Exception ignored) {
        }
        try {
            invoice.setOnClickListener(new OnClick());
        } catch (Exception ignored) {
        }
        try {
            payment.setOnClickListener(new OnClick());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onAuthChange() {
        int visibility;
        if (User.hasCharge() && App.isInternet) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        price.setVisibility(visibility);
        invoice.setVisibility(visibility);
        payment.setVisibility(visibility);
    }
}