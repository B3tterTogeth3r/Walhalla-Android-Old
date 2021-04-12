package de.walhalla.app.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.dialog.CheckboxDialog;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.interfaces.CustomClickListener;
import de.walhalla.app.interfaces.UploadListener;
import de.walhalla.app.models.Accounting;
import de.walhalla.app.utils.Database;
import de.walhalla.app.utils.Variables;

public class BalanceFragment extends CustomFragment implements View.OnClickListener,
        ChosenSemesterListener, CustomClickListener, UploadListener {

    private final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private final CustomClickListener listener = this;
    private final String which;
    private TableLayout tableLayout;
    private Button chooseSemester;
    private ImageButton addNewEntry;

    public BalanceFragment() {
        this.which = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        if (which == null) //Show all the entries
        {
            ScrollView scrollView = new ScrollView(getContext());

            chooseSemester = new Button(getContext());
            chooseSemester.setText(App.getChosenSemester().getLong());
            chooseSemester.setOnClickListener(this);

            RelativeLayout header = new RelativeLayout(getContext());
            TextView title = new TextView(getContext());
            title.setText(R.string.menu_account);
            title.setId(R.id.program_edit);
            title.setTextSize((int) (10 * scale + 0.75f));
            header.addView(title);

            //If the logged in user has a charge, show button
            addNewEntry = new ImageButton(getContext());
            header.addView(addNewEntry);
            addNewEntry.setBackgroundResource(R.color.background);
            addNewEntry.setImageResource(R.drawable.ic_add);
            addNewEntry.setOnClickListener(this);
            addNewEntry.setVisibility(View.GONE);
            RelativeLayout.LayoutParams functionParams = (RelativeLayout.LayoutParams) addNewEntry.getLayoutParams();
            functionParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            functionParams.addRule(RelativeLayout.LEFT_OF, title.getId());
            addNewEntry.setLayoutParams(functionParams);

            LinearLayout linearLayout = view.findViewById(R.id.fragment_container);
            linearLayout.addView(header);
            linearLayout.addView(chooseSemester);
            tableLayout = new TableLayout(getContext());
            scrollView.addView(tableLayout);
            linearLayout.addView(scrollView);

            if (User.isXXX()) {
                addNewEntry.setVisibility(View.VISIBLE);
            }

            loadTable();
        }
        return view;
    }

    public void loadTable() {
        requireActivity().runOnUiThread(() -> {
            ArrayList<Accounting> arrayList;
            arrayList = Database.getCashboxArrayList(App.getChosenSemester());
            int sizeArrayList = arrayList.size();
            tableLayout.removeAllViewsInLayout();
            float totalExpense = 0, totalIncome = 0;
            for (int i = 0; i < sizeArrayList; i++) {
                Accounting current = arrayList.get(i);
                TableRow row = new TableRow(getContext());
                TextView date = new TextView(getContext());
                date.setText(current.getDateFormat());
                row.addView(date);

                TextView expense = new TextView(getContext());
                expense.setText(current.getExpenseFormat());
                totalExpense = totalExpense + current.getExpense();
                row.addView(expense);

                TextView income = new TextView(getContext());
                income.setText(current.getIncomeFormat());
                totalIncome = totalIncome + current.getIncome();
                row.addView(income);

                TextView event = new TextView(getContext());
                event.setText(current.getEvent());
                row.addView(event);

                TextView purpose = new TextView(getContext());
                purpose.setText(current.getPurpose());
                row.addView(purpose);

                TextView add = new TextView(getContext());
                add.setText(current.getAdd());
                row.addView(add);
                //Row onClickListener for Details, maybe don't show everything here in the first place
                LayerDrawable border = getBorders(Color.WHITE, 2, 2);
                row.setBackground(border);
                tableLayout.addView(row);
                row.setOnClickListener(v -> listener.customOnClick(current));
            }
        });
    }

    @Override
    public void customOnClick(@NotNull Accounting current) {
        String TAG = "BalanceFragment";
        Log.i(TAG, current.getId() + "");
        CheckboxDialog dialog = new CheckboxDialog(requireActivity(), current, Variables.DETAILS, this);
        dialog.show();
    }

    protected LayerDrawable getBorders(int bgColor, int top, int bottom) {
        // Initialize new color drawables R.color.black
        ColorDrawable borderColorDrawable = new ColorDrawable(getResources().getColor(R.color.black, null));
        ColorDrawable backgroundColorDrawable = new ColorDrawable(bgColor);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                backgroundColorDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                2, // Number of pixels to add to the left bound [left border]
                top, // Number of pixels to add to the top bound [top border]
                2, // Number of pixels to add to the right bound [right border]
                bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }

    @Override
    public void changeSemesterDone() {
        chooseSemester.setText(App.getChosenSemester().getLong());
        loadTable();
    }

    @Override
    public void onClick(View v) {
        if (v == chooseSemester) {
            ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
            changeSem.show(getParentFragmentManager(), null);
        } else if (v == addNewEntry) {
            CheckboxDialog dialog = new CheckboxDialog(requireActivity(), null, Variables.ADD, this);
            dialog.show();
        }
    }

    @Override
    public void onDatabaseUploadDone(boolean successful) {
        loadTable();
    }

    @Override
    public void onAuthChange() {

    }
}
