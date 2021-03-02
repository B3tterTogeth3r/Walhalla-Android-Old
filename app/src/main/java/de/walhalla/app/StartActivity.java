package de.walhalla.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.firebase.DownloadDoneListener;
import de.walhalla.app.firebase.Firebase;
import de.walhalla.app.models.Semester;
import de.walhalla.app.threads.runnable.CheckInternetRunnable;
import de.walhalla.app.utils.Variables;

public class StartActivity extends AppCompatActivity implements DownloadDoneListener, Firebase.Chargen {
    private static final String TAG = "StartActivity";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static DownloadDoneListener listener;

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
        setContentView(R.layout.splash_screen);
        listener = this;
        Log.i(TAG, "StartActivity should show the shield.");

        final Thread checkInternet = new Thread(new CheckInternetRunnable());
        checkInternet.start();

        ArrayList<Semester> semList = Variables.SEMESTER_ARRAY_LIST;
        Variables.setFirebase();

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
        currentChargen();
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
                    Log.i(TAG, currentChargen.size() + " is the current size of currentChargen");
                });
    }
}