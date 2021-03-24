package de.walhalla.app.fragments.program;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
            String title = getString(R.string.program) + " " + getString(R.string.of) + " " + App.getChosenSemester().getShort();
            toolbar.setTitle(title);
            //Set the subtitle and make it clickable to change the displayed semester
            //TODO make the String inline with the image
            SpannableString spannableString = new SpannableString("@ " + getString(R.string.change_semester));
            try {
                Drawable d = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down);
                Objects.requireNonNull(d).setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                Drawable dw = DrawableCompat.wrap(d);
                DrawableCompat.setTint(dw, requireContext().getColor(R.color.whiteish));
                ImageSpan span = new ImageSpan(dw, ImageSpan.ALIGN_BASELINE);
                spannableString.setSpan(span, spannableString.toString().indexOf("@"), spannableString.toString().indexOf("@") + 1, 0);
                toolbar.setSubtitleTextColor(requireContext().getColor(R.color.whiteish));
                toolbar.setSubtitle(spannableString);
                //Add the filter icon on the right side
                Drawable unwrapped = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_filter_list);
                Drawable wrapped = DrawableCompat.wrap(Objects.requireNonNull(unwrapped));
                DrawableCompat.setTint(wrapped, Color.WHITE);
                toolbar.setOverflowIcon(wrapped);
                toolbar.setOnClickListener(new OnClick("title"));
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.program_filter);
            } catch (NullPointerException npe) {
                Log.d(TAG, "toolbar could not be set", npe);
            }

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
                } catch (NullPointerException e) {
                    Log.d(TAG, "add a new Event.", e);
                }
                return true;
            });
        } catch (Exception e) {
            Log.d(TAG, "Setting the toolbar was unsuccessful", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toolbar.getMenu().clear();
    }
}
