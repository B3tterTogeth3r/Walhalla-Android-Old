package de.walhalla.app.fragments.program;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.Nullable;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.program.ui.Edit;
import de.walhalla.app.fragments.program.ui.Show;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Event;

@SuppressLint("StaticFieldLeak")
public class Fragment extends CustomFragment implements ChosenSemesterListener {
    protected static final String TAG = "program.fragment";

    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static Fragment f;
    protected static Event event, toUpload;
    protected static Toolbar toolbar;

    public Fragment() {
        event = new Event();
        toUpload = new Event();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Fragment.f = this;

        toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbarContent();

        //mSceneRoot, mSceneRoot.findViewById(R.id.layout_root));
        TableLayout content = new TableLayout(getContext());
        content.setStretchAllColumns(true);

        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);

        linearLayout.addView(content);
        content.removeAllViewsInLayout();

        new Show();
        ScrollView sc = new ScrollView(getContext());
        sc.addView(Show.load(false, false));
        content.addView(sc);

        return view;
    }

    @Override
    public void changeSemesterDone() {
        toolbarContent();
        Show.listener.eventChanged();
    }

    @Override
    public void onAuthChange() {
        toolbarContent();
    }

    public void toolbarContent() {
        try {
            //Set the subtitle and make it clickable to change the displayed semester
            LinearLayout subtitle = toolbar.findViewById(R.id.custom_title);
            subtitle.setVisibility(View.VISIBLE);
            //subtitle.setOnClickListener(v -> Log.d(TAG, "toolbar got clicked"));
            TextView title = subtitle.findViewById(R.id.action_bar_title);
            title.setText(String.format("%s %s %s", getString(R.string.program), getString(R.string.of), App.getChosenSemester().getShort()));
            toolbar.setOnClickListener(new OnClick(this));
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.program_filter);

            if (!(User.isLogIn() && User.hasCharge())) {
                toolbar.getMenu().removeItem(R.id.action_add);
                toolbar.getMenu().removeItem(R.id.action_draft);
            }
            if (!User.isLogIn()) {
                toolbar.getMenu().clear();
            }

            toolbar.setOnMenuItemClickListener(item -> {
                try {
                    if (item.getItemId() == R.id.action_add) {
                        Log.i(TAG, "add a new Event.");
                        Edit.display(f.getParentFragmentManager(), null, null);
                    } else if (item.getItemId() == R.id.action_private) {
                        Show.setBooleans(true, false);
                    } else if (item.getItemId() == R.id.action_draft) {
                        Show.setBooleans(false, true);
                    } else if (item.getItemId() == R.id.action_public) {
                        Show.setBooleans(false, false);
                    }
                    return true;
                } catch (NullPointerException e) {
                    Log.d(TAG, "add a new Event.", e);
                    return false;
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Setting the toolbar was unsuccessful", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            toolbar.getMenu().clear();
            toolbar.findViewById(R.id.custom_title).setVisibility(View.GONE);
            toolbar.findViewById(R.id.custom_title).setOnClickListener(null);
        } catch (Exception ignored) {
        }
    }
}
