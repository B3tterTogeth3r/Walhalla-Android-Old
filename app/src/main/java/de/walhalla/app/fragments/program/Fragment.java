package de.walhalla.app.fragments.program;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.program.ui.Show;
import de.walhalla.app.fragments.program.ui.Topline;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.interfaces.NumberpickerCompleteListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;

@SuppressLint("StaticFieldLeak")
public class Fragment extends CustomFragment implements ChosenSemesterListener,
        NumberpickerCompleteListener {
    protected static final String TAG = "program.fragment";

    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    protected static Fragment f;
    protected static Event event, toUpload;
    protected static ImageButton add;
    protected static Button chooseSemester;
    protected static float value = 1f;
    protected static float px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            App.getContext().getResources().getDisplayMetrics()
    );
    public static ArrayList<Helper> allhelpers = new ArrayList<>();

    public Fragment() {
        event = new Event();
        toUpload = new Event();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Fragment.f = this;

        //mSceneRoot, mSceneRoot.findViewById(R.id.layout_root));
        TableLayout content = new TableLayout(getContext());
        content.setStretchAllColumns(true);

        LinearLayout linearLayout = view.findViewById(R.id.fragment_container);

        linearLayout.addView(content);
        content.removeAllViewsInLayout();

        content.addView(Topline.load());

        new Show();
        content.addView(Show.load());


        try {
            add.setOnClickListener(new OnClick(add));
        } catch (Exception ignore) {
        }
        chooseSemester.setOnClickListener(new OnClick(chooseSemester));

        return view;
    }

    @Override
    public void changeSemesterDone() {
        Show.listener.eventChanged();
        chooseSemester.setText(App.getChosenSemester().getLong());
    }

    @Override
    public void onAuthChange() {
        Topline.buttonVisibility();
    }
}
