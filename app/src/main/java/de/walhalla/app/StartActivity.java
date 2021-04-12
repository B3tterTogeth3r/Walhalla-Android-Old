package de.walhalla.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import de.walhalla.app.firebase.DownloadDoneListener;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.utils.Variables;

public class StartActivity extends AppCompatActivity implements DownloadDoneListener, Firebase.Chargen {
    private static final String TAG = "StartActivity";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static DownloadDoneListener listener;
    //TODO activate all the necessary change listeners for public accessible pages

    @Override
    public void onDone() {
        /*Go to MainActivity */
        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_screen);
        listener = this;
        Log.i(TAG, "StartActivity should show the shield.");

        /* Ask for CAMERA permission */
        int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
        /* Ask for CALENDAR permission */
        int hasCalendarPermission = checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR);
        if (hasCalendarPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    124);
        }

        if (!Variables.setFirebase()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle(R.string.error_title)
                    .setMessage(R.string.error_close_app)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        ((Activity) getApplicationContext()).finish();
                    })
                    .create();
            builder.show();
        } else {
            try {
                Log.d(TAG, "Creating successfully finished.");
                currentChargen();
            } catch (Exception e) {
                Log.d(TAG, "Error on App start", e);
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle(R.string.error_title)
                        .setMessage(R.string.error_close_app)
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                            ((Activity) getApplicationContext()).finish();
                        })
                        .create();
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(App.getContext(), "CAMERA allowed", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Permission Denied
                Toast.makeText(App.getContext(), "CAMERA Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void currentChargen() {
        String current_semester_id = String.valueOf(App.getCurrentSemester().getID());
        Variables.Firebase.FIRESTORE.collection("Semester")
                .document(current_semester_id)
                .collection("Chargen")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> currentChargen = new ArrayList<>();
                    for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                        currentChargen.add((String) s.get("uid"));
                    }
                    App.setCurrentChargen(currentChargen);
                    admins();
                });
    }

    public void admins() {
        Variables.Firebase.FIRESTORE
                .collection("Editors")
                .document("private")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        Map<String, Object> admins = documentSnapshot.getData();
                        if (admins != null && !admins.isEmpty()) {
                            Map<String, Object> list = (Map<String, Object>) admins.get("roles");
                            User.setAdmins(list);
                        }
                        onDone();
                    } catch (Exception ignored) {
                    }
                });
    }
}