package de.walhalla.app.fragments.news.ui;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.R;
import de.walhalla.app.fragments.news.Fragment;

public class Add extends Fragment {
    public Add() {
    }

    @NotNull
    public static TableLayout load() {
        TableLayout tableLayout = new TableLayout(f.getContext());
        title = new EditText(f.getContext());
        content = new EditText(f.getContext());
        ShowImg = new ImageButton(f.getContext());

        title.setHint(R.string.title);
        content.setHint(R.string.description);
        content.setMinHeight((int) scale * 2);

        ShowImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ShowImg.setImageResource(R.drawable.wappen_2017);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scale * 250);
        tableLayout.addView(title);
        tableLayout.addView(content);
        tableLayout.addView(ShowImg, params);

        return tableLayout;
    }
}
