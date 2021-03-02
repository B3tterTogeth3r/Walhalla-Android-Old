package de.walhalla.app.fragments.program.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.fragments.program.Fragment;

public class Topline extends Fragment {
    private final static float scale = App.getContext().getResources().getDisplayMetrics().density;

    public Topline() {
    }

    @NotNull
    public static TableLayout load() {
        TableLayout tableLayout = new TableLayout(f.getContext());
        TableRow firstRow = new TableRow(f.getContext());
        TableRow secondRow = new TableRow(f.getContext());
        RelativeLayout start = new RelativeLayout(f.getContext());
        TextView title = new TextView(f.getContext());

        chooseSemester = new Button(f.getContext());
        add = new ImageButton(f.getContext());

        title.setVisibility(View.GONE);
        add.setVisibility(View.GONE);

        //Button to close Details or abort a new entry

        title.setId(R.id.program_edit);
        add.setId(R.id.add);

        start.addView(title);
        start.addView(add);
        secondRow.addView(chooseSemester);

        //Title
        title.setText(R.string.menu_program);
        title.setTextSize((int) (10 * scale + 0.75f));
        title.setPadding(5, 5, 0, 5);

        add.setImageResource(R.drawable.ic_add);

        add.setBackgroundColor(f.getResources().getColor(R.color.background, null));

        RelativeLayout.LayoutParams functionParams = (RelativeLayout.LayoutParams) add.getLayoutParams();
        functionParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        functionParams.addRule(RelativeLayout.LEFT_OF, title.getId());
        add.setLayoutParams(functionParams);

        /*
        _______________
        |Title       +|
        |SemChangeBT  |
        ---------------
         */
        title.setVisibility(View.VISIBLE);
        buttonVisibility();

        title.setText(R.string.menu_program);
        chooseSemester.setText(App.getChosenSemester().getLong());

        firstRow.removeAllViewsInLayout();
        firstRow.addView(start);
        tableLayout.removeAllViewsInLayout();
        tableLayout.addView(firstRow);
        secondRow.removeAllViewsInLayout();
        secondRow.addView(chooseSemester);
        tableLayout.addView(secondRow);
        tableLayout.setStretchAllColumns(true);
        return tableLayout;
    }

    public static void buttonVisibility() {
        if (User.isLogIn() && User.isX()) {
            add.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
        }
    }
}
