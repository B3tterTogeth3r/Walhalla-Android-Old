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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.models.Event;
import de.walhalla.app.utils.Variables;

public class Fragment extends CustomFragment {
    private static final String TAG = "HomeFragment";
    LinearLayout greeting, notes;
    RelativeLayout signRow;
    TextView student_x, phil_x;
    ArrayList<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        greeting = view.findViewById(R.id.home_layout_greetings);
        notes = view.findViewById(R.id.home_layout_notes);
        signRow = view.findViewById(R.id.home_greetings_relative);
        student_x = view.findViewById(R.id.charge_x);
        phil_x = view.findViewById(R.id.philister_x);

        if (App.getCurrentSemester() != null) {
            displayCurrentGreeting(App.getCurrentSemester().getID());
        }

        return view;
    }

    @SuppressWarnings("unchecked")
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
            title.setPadding(4, 4, 4, 4);
            title.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
            notes.addView(title);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadGreeting(@NotNull List<Object> text) {
        int size = text.size();
        Map<String, String> sign = (Map<String, String>) text.get(size - 1);
        text.remove(size - 1);
        size--;
        greeting.removeAllViewsInLayout();
        greeting.setOrientation(LinearLayout.VERTICAL);
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

    @Override
    public void onAuthChange() {

    }
}
