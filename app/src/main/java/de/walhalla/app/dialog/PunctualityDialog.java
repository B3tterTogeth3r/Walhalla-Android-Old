package de.walhalla.app.dialog;

import android.app.AlertDialog;
import android.content.Context;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.walhalla.app.R;
import de.walhalla.app.interfaces.CouleurTimePickerListener;
import de.walhalla.app.utils.Variables;

public class PunctualityDialog extends AlertDialog.Builder {
    private static CharSequence[] list;
    private static String kind;
    private static CouleurTimePickerListener listener;
    private String result;

    public PunctualityDialog(Context context) {
        super(context);
        setCancelable(false);
        setSingleChoiceItems(list, -1, (dialog, which) -> result = list[which].toString());
        //setItems(punctuality, null);
        setPositiveButton(R.string.send, (dialog, which) -> {
            //Open collar dialog
            if (kind.equals("punctuality")) {
                if (result == null || result.isEmpty()) {
                    result = "ct";
                }
                listener.punctualityDone(result);
                PunctualityDialog.load(context, "collar", listener);
                dialog.dismiss();
            } else if (kind.equals("collar")) {
                if (result == null || result.isEmpty()) {
                    result = "";
                }
                listener.collarDone(result);
                dialog.dismiss();
            }
        });
    }

    public static void load(Context context, String kind, CouleurTimePickerListener listener) {
        PunctualityDialog.kind = kind;
        PunctualityDialog.listener = listener;
        Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document(kind)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> map = documentSnapshot.getData();
                        Set<String> punct = Objects.requireNonNull(map).keySet();
                        list = new CharSequence[punct.size()];
                        punct.toArray(list);
                        PunctualityDialog dialog = new PunctualityDialog(context);
                        dialog.show();
                    }
                });
    }
}
