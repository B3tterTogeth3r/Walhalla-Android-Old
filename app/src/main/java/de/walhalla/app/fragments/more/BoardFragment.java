package de.walhalla.app.fragments.more;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import de.walhalla.app.R;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.fragments.CustomFragment;
import de.walhalla.app.utils.Variables;

public class BoardFragment extends CustomFragment {
    private static final String TAG = "BoardFragment";
    private ImageView diashow;
    private ImageButton diashow_right, diashow_left;
    private ArrayList<String> picture_names;
    private TextView diashow_description;
    private View view;


    public BoardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_board, container, false);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(R.string.menu_more_board);
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setSubtitle("");

        Log.i(TAG, " is the current fragment");
        diashow = view.findViewById(R.id.diashow_image);
        diashow_description = view.findViewById(R.id.diashow_description);
        diashow_left = view.findViewById(R.id.diashow_previous);
        diashow_right = view.findViewById(R.id.diashow_next);
        loadDiashow();
        loadBoard();

        return view;
    }

    @SuppressWarnings("unchecked")
    private void loadBoard() {
        TableLayout layout = view.findViewById(R.id.board_list);
        layout.removeAllViews();
        Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document("Chargen")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "content Download complete");
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null)
                        for (int i = 0; i < data.size(); i++) {
                            TableRow charge = new TableRow(getContext());
                            TextView title = new TextView(getContext());
                            TextView description = new TextView(getContext());
                            title.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY);
                            description.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY);
                            charge.addView(title);
                            charge.addView(description);
                            try {
                                Map<String, Object> current = (Map<String, Object>) data.get(String.valueOf(i));
                                title.setText((String) current.get("name"));
                                description.setText((String) current.get("description"));
                                layout.addView(charge);
                            } catch (Exception e) {
                                Log.d(TAG, "parsing data from the Snapshot went wrong.", e);
                            }
                        }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Downloading the Chargen content did not work", e));
    }

    private void loadDiashow() {
        diashow_right.setVisibility(View.GONE);
        diashow_left.setVisibility(View.GONE);
        Variables.Firebase.FIRESTORE
                .collection("Diashow")
                .document("more_board")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        //noinspection unchecked
                        picture_names = (ArrayList<String>) documentSnapshot.get("picture_names");
                        if (picture_names != null && picture_names.size() != 0) {
                            //download only the first image
                            downloadImage(picture_names.get(0));
                        }
                    }
                });
    }

    private void downloadImage(String image_name) {

        StorageReference image = FirebaseStorage.getInstance().getReference(image_name);
        image.getBytes(Variables.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            diashow.setImageBitmap(Firebase.addWatermark(bmp));
            Variables.Firebase.FIRESTORE.collection("Data")
                    .whereEqualTo("name", image_name)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                diashow_description.setText((String) documentSnapshot.get("title"));
                            }
                        } else {
                            diashow_description.setText(image_name);
                        }
                    })
                    .addOnFailureListener(e -> diashow_description.setText(image_name));
        }).addOnFailureListener(e ->
                Log.e(TAG, "Image download unsuccessful", e));
    }
}
