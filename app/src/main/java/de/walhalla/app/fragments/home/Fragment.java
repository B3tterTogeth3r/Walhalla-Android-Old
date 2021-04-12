package de.walhalla.app.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.Diashow;
import de.walhalla.app.utils.Variables;

public class Fragment extends CustomFragment {
    private static final String TAG = "HomeFragment";
    private LinearLayout greeting, notes;
    private RelativeLayout signRow;
    private TextView student_x, phil_x;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.app_name);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        greeting = view.findViewById(R.id.home_layout_greetings);
        notes = view.findViewById(R.id.home_layout_notes);
        signRow = view.findViewById(R.id.home_greetings_relative);
        student_x = view.findViewById(R.id.charge_x);
        phil_x = view.findViewById(R.id.philister_x);

        //initiate diashow
        RelativeLayout diashow = view.findViewById(R.id.home_diashow);
        diashow.removeAllViewsInLayout();
        diashow.addView(new Diashow(getContext()).show("home"));
        if (App.getCurrentSemester() != null) {
            displayCurrentGreeting(App.getCurrentSemester().getID());
        }
        //TODO else fill layout with the welcome text still in the strings

        return view;
    }

    /**
     * Download the greeting from the first page of the print program to display it. <br/>
     * Only the current greeting will be displayed.
     *
     * @param id the value of the semester from which the greeting is to be downloaded.
     */
    private void displayCurrentGreeting(int id) {
        Variables.Firebase.FIRESTORE
                .collection("Semester")
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            List<Object> text = (List<Object>) documentSnapshot.get("greeting");
                            if (text != null) {
                                loadGreeting(text);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "While downloading greeting an error occurred.", e);
                        }
                        try {
                            List<Object> text = (List<Object>) documentSnapshot.get("notes");
                            if (text != null) {
                                loadNotes(text);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "While downloading notes an error occurred.", e);
                        }
                    }
                });
    }

    /**
     * Sets the current notes of the Semester.
     *
     * @param text The List with all notes.
     */
    private void loadNotes(@NotNull List<Object> text) {
        int size = text.size();
        notes.removeAllViewsInLayout();
        notes.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(getContext());
        title.setText(R.string.home_notes);
        title.setPadding(4, 4, 4, 4);
        title.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        notes.addView(title);
        for (int i = 0; i < size; i++) {
            title = new TextView(getContext());
            title.setText(text.get(i).toString());
            title.setPadding(4, 4, 4, 16);
            title.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
            notes.addView(title);
        }
    }

    /**
     * Sets the greeting of the selected semester. The first row
     * is the salutation, the last row the closing formula
     *
     * @param text The list with all the paragraphs of the greeting
     */
    private void loadGreeting(@NotNull List<Object> text) {
        int size = text.size();
        Map<String, String> sign = (Map<String, String>) text.get(size - 1);
        text.remove(size - 1);
        size--;
        greeting.removeAllViewsInLayout();
        greeting.setOrientation(LinearLayout.VERTICAL);
        TextView header = (TextView) getLayoutInflater().inflate(R.layout.text_view_history_title, null);
        header.setText(R.string.greeting);
        greeting.addView(header);

        for (int i = 0; i < size - 1; i++) {
            TextView title = new TextView(getContext());
            title.setText(text.get(i).toString());
            title.setPadding(4, 4, 4, 4);
            title.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
            greeting.addView(title);
        }
        TextView bottom = new TextView(getContext());
        bottom.setText(text.get(size - 1).toString());
        bottom.setPadding(4, 4, 4, 4);
        bottom.setTextAppearance(R.style.TextAppearance_AppCompat_Body2);
        greeting.addView(bottom);
        signRow.setVisibility(View.VISIBLE);
        student_x.setText(sign.get("Aktivensenior"));
        phil_x.setText(sign.get("Philistersenior"));
        greeting.addView(signRow);
    }
}
