package de.walhalla.app.fragments.more;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.Diashow;
import de.walhalla.app.utils.Variables;

public class AboutUsFragment extends CustomFragment {
    private static final String TAG = "AboutUsFragment";
    private LinearLayout whoWeAre, whoWeAreLookingFor, allInAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.menu_about_us);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        whoWeAre = view.findViewById(R.id.about_us_who_we_are);
        whoWeAreLookingFor = view.findViewById(R.id.about_us_who_we_are_looking_for);
        allInAll = view.findViewById(R.id.about_us_all_in_all);

        Variables.Firebase.FIRESTORE
                .collection("Sites")
                .document("About_us")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Map<String, Object> content = (Map<String, Object>) documentSnapshot.get("Wer wir sind");
                            if (content != null) {
                                load(content, whoWeAre, getString(R.string.menu_about_us_section_1));
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "While downloading about us - who we are an error occurred.", e);
                        }
                        try {
                            Map<String, Object> content = (Map<String, Object>) documentSnapshot.get("Wen wir suchen");
                            if (content != null) {
                                load(content, whoWeAreLookingFor, getString(R.string.menu_about_us_section_2));
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "While downloading about us - who we are an error occurred.", e);
                        }
                        try {
                            Map<String, Object> content = (Map<String, Object>) documentSnapshot.get("Kurz und knapp: Das findest du bei uns");
                            if (content != null) {
                                load(content, allInAll);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "While downloading about us - who we are an error occurred.", e);
                        }
                    }
                });

        return view;
    }

    private void load(@NotNull Map<String, Object> content, @NotNull LinearLayout layout, String name) {
        int size = content.size();
        layout.removeAllViewsInLayout();
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(getContext(), null, R.style.Settings_TV_Title);
        title.setText(name);
        title.setTextSize(20f);
        title.setAllCaps(true);
        layout.addView(title);
        for (int i = 0; i < size; i++) {
            try {
                String text = (String) (content.get(String.valueOf(i)));
                TextView line = new TextView(getContext(), null, R.style.Settings_TV_Sub);
                line.setText(text);
                layout.addView(line);
            } catch (Exception e) {
                Log.d(TAG, "Value is not a String at position " + i);
                //Import diashow with the images etc
                layout.addView(new Diashow(getContext()).show("about_us"));
            }
        }
    }

    private void load(@NotNull Map<String, Object> content, @NotNull LinearLayout layout) {
        int size = content.size();
        layout.removeAllViewsInLayout();
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(getContext(), null, R.style.Settings_TV_Title);
        title.setText(R.string.menu_about_us_section_3);
        title.setTextSize(20f);
        title.setAllCaps(true);
        layout.addView(title);
        for (int i = 0; i < size; i++) {
            try {
                String line = (String) (content.get(String.valueOf(i)));
                CheckBox last = new CheckBox(getContext());
                last.setChecked(true);
                last.setClickable(false);
                last.setContextClickable(false);
                last.setCursorVisible(false);
                last.setEnabled(true);
                last.setText(line);
                layout.addView(last);
                last.setTextAppearance(R.style.Settings_TV_Sub);
            } catch (Exception e) {
                Log.d(TAG, "Value is not a String at position " + i, e);
            }
        }
    }

}
