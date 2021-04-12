package de.walhalla.app.fragments.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.Map;

import de.walhalla.app.R;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.fragments.Sites;
import de.walhalla.app.utils.Variables;

@SuppressWarnings("ConstantConditions")
public class RoomsFragment extends CustomFragment {
    private final static String TAG = "RoomsFragment";
    private LinearLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.menu_rooms);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        LinearLayout viewContainer = view.findViewById(R.id.fragment_container);
        ScrollView sc = new ScrollView(getContext());
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViewsInLayout();

        Variables.Firebase.FIRESTORE
                .collection("Sites")
                .document("rooms")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        for (Map.Entry<String, Object> line : documentSnapshot.getData().entrySet()) {
                            Map<String, Object> content = (Map<String, Object>) line.getValue();
                            if (content != null) {
                                Sites.createArea(getContext(), layout, content);
                            }
                        }
                    }
                });
        sc.addView(layout);
        viewContainer.addView(sc);

        return view;
    }
}
