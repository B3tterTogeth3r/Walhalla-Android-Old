package de.walhalla.app.fragments.program.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.dialog.AccountPlanningDialog;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.dialog.ErrorDialog;
import de.walhalla.app.dialog.JobPickerDialog;
import de.walhalla.app.dialog.PunctualityDialog;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.interfaces.CouleurTimePickerListener;
import de.walhalla.app.interfaces.NumberPickerCompleteListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Edit extends DialogFragment implements OnMapReadyCallback, View.OnClickListener,
        CouleurTimePickerListener, NumberPickerCompleteListener, ChosenSemesterListener {
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private static final String TAG = "Details of Program-Event";
    protected static Marker marker;
    private static Details detailsDialog;
    private final Event event;
    private final String whichOne;
    private Toolbar toolbar;
    private EditText title, description;
    private Button timeStart, timeEnd, accounting, semester, tasks;
    private TextView accBefore, accAfter;
    private String result;
    private CheckBox meeting, startAdh;
    private FrameLayout frameLayout;
    private Map<String, Object> accountingElements;

    public Edit(Event event) {
        //Edit an existing event
        this.event = event;
        this.whichOne = Variables.EDIT;
    }

    public Edit() {
        //Add a new Event
        this.whichOne = Variables.ADD;
        this.event = new Event();
    }

    public static void display(FragmentManager fragmentManager, @Nullable Event event, Details detailsDialog) {
        Edit editDialog;
        Edit.detailsDialog = detailsDialog;
        if (event == null) {
            editDialog = new Edit();
        } else {
            editDialog = new Edit(event);
        }
        editDialog.show(fragmentManager, TAG);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup containter, Bundle savedInstanceState) {
        super.onCreateView(inflater, containter, savedInstanceState);
        View view = inflater.inflate(R.layout.program_edit, containter, false);
        toolbar = view.findViewById(R.id.program_details_close);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout = view.findViewById(R.id.program_details_layout);

        toolbar.setNavigationOnClickListener(v -> {
            //Show a dialog with question to whether save it as a draft or just dismiss; only if whichOne == ADD
            if (whichOne.equals(Variables.ADD)) {
                AlertDialog.Builder draftSaver = new AlertDialog.Builder(getContext());
                draftSaver.setTitle(R.string.abort)
                        .setMessage(R.string.program_abort_sure)
                        .setPositiveButton(R.string.yes, (dialog, which) -> sendDraft())
                        .setNegativeButton(R.string.no, (dialog, which) -> dialogDismiss());
                draftSaver.show();
            }
        });
        toolbar.setTitle(R.string.program_details);
        Drawable unwrapped = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_menu_add);
        Drawable wrapped = DrawableCompat.wrap(unwrapped);
        DrawableCompat.setTint(wrapped, Color.WHITE);
        toolbar.setOverflowIcon(wrapped);
        toolbar.inflateMenu(R.menu.program_add_send);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send) {
                Log.i(TAG, "Event is going to be public");
                sendPublic();
            } else if (item.getItemId() == R.id.action_send_private) {
                Log.i(TAG, "Event is going to be still in private mode");
                sendPrivate();
            } else if (item.getItemId() == R.id.action_draft) {
                Log.i(TAG, "Event is going to be still in draft mode");
                sendDraft();
            } else {
                dialogDismiss();
            }
            return true;
        });

        title = view.findViewById(R.id.program_edit_title);
        description = view.findViewById(R.id.program_edit_description);
        timeStart = view.findViewById(R.id.program_edit_start_time);
        timeEnd = view.findViewById(R.id.program_edit_end_time);
        accounting = view.findViewById(R.id.program_edit_accounting);
        accBefore = view.findViewById(R.id.program_edit_acc_before);
        accAfter = view.findViewById(R.id.program_edit_acc_after);
        TextView accCurr = view.findViewById(R.id.program_edit_acc_current);
        semester = view.findViewById(R.id.program_semester);
        meeting = view.findViewById(R.id.program_meeting);
        startAdh = view.findViewById(R.id.program_start_place);
        tasks = view.findViewById(R.id.program_edit_tasks);

        timeStart.setOnClickListener(this);
        timeEnd.setOnClickListener(this);
        accounting.setOnClickListener(this);
        semester.setOnClickListener(this);
        meeting.setOnClickListener(this);
        tasks.setOnClickListener(this);

        if (whichOne.equals(Variables.ADD)) {
            startAdh.setOnClickListener(this);
        } else {
            startAdh.setEnabled(false);
            startAdh.setClickable(false);
        }

        frameLayout = new FrameLayout(requireContext());
        frameLayout.setId(R.id.maps_fragment);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scale * 400);
        linearLayout.addView(frameLayout, params);
        //TODO Make the parent fragment not scrollable while this one is touched
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(frameLayout.getId(), supportMapFragment).commit();
        try {
            supportMapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(getContext(), "MapAPI wollte nicht so", Toast.LENGTH_SHORT).show();
        }

        //Fill fields with the content
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        Calendar c = Calendar.getInstance();
        c.setTime(event.getStart().toDate());
        float hourFl = c.get(Calendar.HOUR_OF_DAY), minuteFl = c.get(Calendar.MINUTE);
        String minute, hour;
        if (c.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + String.format(Variables.LOCALE, "%.0f", hourFl);
        } else {
            hour = String.format(Variables.LOCALE, "%.0f", hourFl);
        }
        if (c.get(Calendar.MINUTE) < 10) {
            minute = "0" + String.format(Variables.LOCALE, "%.0f", minuteFl);
        } else {
            minute = String.format(Variables.LOCALE, "%.0f", minuteFl);
        }
        String helper = c.get(Calendar.DAY_OF_MONTH) + "." +
                Variables.MONTHS[c.get(Calendar.MONTH)] + "." +
                c.get(Calendar.YEAR) + " " + hour + ":" + minute + " " +
                getResources().getString(R.string.clock);
        timeStart.setText(helper);

        c.setTime(event.getEnd().toDate());
        hourFl = c.get(Calendar.HOUR_OF_DAY);
        minuteFl = c.get(Calendar.MINUTE);
        if (c.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + String.format(Variables.LOCALE, "%.0f", hourFl);
        } else {
            hour = String.format(Variables.LOCALE, "%.0f", hourFl);
        }
        if (c.get(Calendar.MINUTE) < 10) {
            minute = "0" + String.format(Variables.LOCALE, "%.0f", minuteFl);
        } else {
            minute = String.format(Variables.LOCALE, "%.0f", minuteFl);
        }
        helper = c.get(Calendar.DAY_OF_MONTH) + "." +
                Variables.MONTHS[c.get(Calendar.MONTH)] + "." +
                c.get(Calendar.YEAR) + " " + hour + ":" + minute + " " +
                getResources().getString(R.string.clock);
        timeEnd.setText(helper);

        try {
            Map<String, Object> budget = event.getBudget();
            accAfter.setText((String) budget.get("after"));
            accBefore.setText((String) budget.get("before"));
            accCurr.setText((String) budget.get("current"));
        } catch (Exception ignored) {
        }

        startAdh.setChecked(whichOne.equals(Variables.ADD));
    }

    private void sendDraft() {
        event.setDraft(true);
        event.setInternal(false);
        upload();
    }

    private void sendPrivate() {
        event.setDraft(false);
        event.setInternal(true);
        upload();
    }

    private void sendPublic() {
        event.setDraft(false);
        event.setInternal(false);
        upload();
    }

    private void upload() {
        try {
            //check for mistakes and if there are none, add it to the local and online database.
            event.setLocation_coordinates(new GeoPoint(marker.getPosition().latitude, marker.getPosition().longitude));
            LatLng latLng = marker.getPosition();
            Geocoder geocoder = new Geocoder(getContext());
            event.setTitle(String.valueOf(title.getText()));
            event.setDescription(String.valueOf(description.getText()));

            try {
                if (Geocoder.isPresent()) {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    for (int i = 0; i < addressList.size(); i++) {
                        String result = addressList.get(i).getThoroughfare() + " "
                                + addressList.get(i).getSubThoroughfare() + ", "
                                + addressList.get(i).getPostalCode() + " "
                                + addressList.get(i).getLocality();
                        event.setLocation_name(result);
                        event.setLocation_coordinates(new GeoPoint(latLng.latitude, latLng.longitude));
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "Couldn't find an address at the selected position", e);
            }
            if (!whichOne.equals(Variables.DELETE)) {
                Event upload = new Event();
                boolean error = false;
                if (whichOne.equals(Variables.ADD)) {
                    //Upload toUpload to the online database and add it to the local ArrayList
                    try {
                        if (event.getStart() == null ||
                                event.getEnd() == null ||
                                event.getPunctuality() == null ||
                                event.getCollar() == null ||
                                event.getTitle() == null) {
                            error = true;
                        }
                        if (event.getLocation_name() == null)
                            event.setLocation_name(Variables.Walhalla.NAME);
                        if (event.getLocation_coordinates() == null)
                            event.setLocation_coordinates(Variables.Walhalla.ADH_LOCATION);
                        upload = (Event) event.clone();
                    } catch (Exception e) {
                        error = true;
                    }
                    //Upload them to the firebase realtime database
                }
                if (whichOne.equals(Variables.EDIT)) {
                    try {
                        upload = (Event) event.clone();
                    } catch (Exception e) {
                        error = true;
                    }
                    //Upload them to the firebase realtime database
                }
                if (!error) {
                    int sem_id = App.getChosenSemester().getID();
                    //Upload
                    if (whichOne.equals(Variables.ADD)) {
                        Variables.Firebase.FIRESTORE.collection("Semester")
                                .document(String.valueOf(sem_id))
                                .collection("Event")
                                .add(upload)
                                .addOnFailureListener(e -> uploadError())
                                .addOnSuccessListener(aVoid -> uploadSuccess());
                    }
                    //Edit
                    else if (whichOne.equals(Variables.EDIT)) {
                        Variables.Firebase.FIRESTORE.collection("Semester")
                                .document(String.valueOf(sem_id))
                                .collection("Event")
                                .document(upload.getId())
                                .update(upload.toMap())
                                .addOnFailureListener(e -> uploadError())
                                .addOnSuccessListener(aVoid -> uploadSuccess());
                    }
                } else {
                    uploadError();
                }
            } else {
                dialogDismiss();
            }
        } catch (Exception exception) {
            Log.d(TAG, "Upload:error", exception);
        }
    }

    private void uploadSuccess() {
        //TODO create a sub collection inside the event with an empty document, if isMeeting() = true
        Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_LONG).show();
        dialogDismiss();
    }

    private void uploadError() {
        Snackbar.make(requireView(), R.string.error_upload, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, v -> {
                })
                .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                .show();
    }

    private void dialogDismiss() {
        if (detailsDialog != null) {
            detailsDialog.dismiss();
        }
        dismiss();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == timeStart) {
                date(Event.START);
            } else if (v == timeEnd) {
                date(Event.END);
            } else if (v == accounting) {
                accounting();
            }
            //Button to select the belonging semester got clicked
            else if (v == semester) {
                ChangeSemesterDialog changeSem = new ChangeSemesterDialog(this);
                changeSem.show(getParentFragmentManager(), null);
            }
            //The Checkbox meeting got selected
            else if (v == meeting) {
                event.setMeeting(meeting.isChecked());
            }
            //The start of the event may be different from the default start
            else if (v == startAdh) {
                //Display the map if the Checkbox isn't checked anymore.
                if (startAdh.isChecked() && whichOne.equals(Variables.ADD)) {
                    frameLayout.setVisibility(View.GONE);
                } else {
                    frameLayout.setVisibility(View.VISIBLE);
                }
            }
            //Show the dialog to choose a task to then assign people to
            else if (v == tasks) {
                assignTasks();
            }
        } catch (Exception e) {
            Log.d(TAG, "In program.edit something went wrong.", e);
        }
    }

    private void assignTasks() {
        Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document("Helper")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        //create dialog and sort list inside
                        JobPickerDialog.load(getContext(), event.getHelp(), documentSnapshot.getData(), this);
                    }
                });
    }

    /**
     * Download the possible ways of accounting and display a numberPicker dialog
     * with them inside. The user chooses one and the result gets saved in the event
     */
    private void accounting() {
        Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document("accounting")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            accountingElements.clear();
                        } catch (Exception ignored) {
                        }
                        accountingElements = documentSnapshot.getData();
                        List<String> names = new ArrayList<>(accountingElements.keySet());
                        Collections.sort(names);
                        if (names.size() != 0) {
                            new AccountPlanningDialog(getContext(), names, Edit.this).show();
                        } else {
                            Log.d(TAG, "names.size ==0");
                        }
                    }
                });
    }

    /**
     * Start date picker with android theme
     *
     * @param kind Save the picked date in the corresponding place
     */
    protected void date(@NotNull String kind) {
        //Get Current Date
        final Calendar c = Calendar.getInstance();
        if (kind.equals(Event.START)) {
            c.setTime(event.getStart().toDate());
        } else if (kind.equals(Event.END)) {
            c.setTime(event.getEnd().toDate());
        }

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    c.set(year, monthOfYear, dayOfMonth);
                    String day = String.valueOf(dayOfMonth);
                    if (Integer.parseInt(day) < 10) {
                        day = "0" + day;
                    }
                    result = day + ". " + Variables.MONTHS[monthOfYear] + " " + year;
                    if (kind.equals(Event.START)) {
                        timeStart.setText(R.string.program_edit_time_start);
                        event.setStart(new Timestamp(c.getTime()));
                    } else if (kind.equals(Event.END)) {
                        timeEnd.setText(R.string.program_dialog_time_end);
                        event.setEnd(new Timestamp(c.getTime()));
                    }
                    time(kind);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * Start time picker with android theme
     *
     * @param kind Save the picked date in the corresponding place
     */
    protected void time(@NotNull String kind) {
        //Get Current Time
        final Calendar c = Calendar.getInstance();
        if (kind.equals(Event.START)) {
            c.setTime(event.getStart().toDate());
        } else if (kind.equals(Event.END)) {
            c.setTime(event.getStart().toDate());
        }

        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    String resultTime;
                    if (minute < 10) {
                        resultTime = hourOfDay + ":" + "0" + minute;
                    } else {
                        resultTime = hourOfDay + ":" + minute;
                    }
                    Calendar cDate = Calendar.getInstance();
                    if (kind.equals(Event.START)) {
                        Date date = event.getStart().toDate();
                        cDate.setTime(date);
                        cDate.set(Calendar.MINUTE, minute);
                        cDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        event.setStart(new Timestamp(cDate.getTime()));
                        result = result + " " + resultTime + " " + getString(R.string.clock);
                        PunctualityDialog.load(getContext(), "punctuality", this);
                    } else if (kind.equals(Event.END)) {
                        //Test for same or later date as start
                        if (c.getTime().before(event.getStart().toDate()) || c.getTime().equals(event.getStart().toDate())) {
                            //Dialog with error message
                            try {
                                ErrorDialog.display(getParentFragmentManager(), getString(R.string.error_time));
                            } catch (Exception ignored) {
                            }
                        } else {
                            Date date = event.getEnd().toDate();
                            cDate.setTime(date);
                            cDate.set(Calendar.MINUTE, minute);
                            cDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            event.setEnd(new Timestamp(cDate.getTime()));
                            result = result + " " + resultTime;
                            timeEnd.setText(result);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    /**
     * Make this dialog full screen
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    /**
     * Make this dialog full screen
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        LatLng place;
        final MarkerOptions markerOptions = new MarkerOptions();
        googleMap.setMaxZoomPreference(15);
        googleMap.setMinZoomPreference(15);
        switch (whichOne) {
            case Variables.ADD:
                place = new LatLng(49.784389, 9.924648);
                markerOptions.position(place)
                        .title("adH Walhallae")
                        .draggable(true);
                marker = googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                googleMap.setOnCameraMoveListener(() -> {
                    double longitude = googleMap.getCameraPosition().target.longitude;
                    double latitude = googleMap.getCameraPosition().target.latitude;
                    LatLng latLng = new LatLng(latitude, longitude);
                    marker.setPosition(latLng);
                });
                break;
            case Variables.EDIT:
                place = new LatLng(event.getLocation_coordinates().getLatitude(), event.getLocation_coordinates().getLongitude());
                markerOptions.position(place)
                        .title(event.getLocation_name())
                        .draggable(true);
                marker = googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                googleMap.setOnCameraMoveListener(() -> {
                    double longitude = googleMap.getCameraPosition().target.longitude;
                    double latitude = googleMap.getCameraPosition().target.latitude;
                    LatLng latLng = new LatLng(latitude, longitude);
                    marker.setPosition(latLng);
                });
                break;
        }

        try {
            if (startAdh.isChecked() && whichOne.equals(Variables.ADD)) {
                frameLayout.setVisibility(View.GONE);
            } else {
                frameLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.d(TAG, "Visibility did not work on the Map", e);
        }
    }

    @Override
    public void notifyOfAccountingDone(String name) {
        Map<String, Object> data = (Map<String, Object>) accountingElements.get(name);
        String format = "€ " + data.get("before").toString() + ",00";
        accBefore.setText(format);
        format = "€ " + data.get("after").toString() + ",00";
        accAfter.setText(format);
        format = getString(R.string.program_dialog_accounting) + ": " + name;
        accounting.setText(format);
        event.setBudget(data);
    }

    @Override
    public void notifyOfTaskDone(@NotNull Map<String, Object> helpers) {
        Log.d(TAG, "notifiyOfTaskDone:size = " + helpers.size());
        event.setHelp(helpers);
    }

    @Override
    public void punctualityDone(@NotNull String punctuality) {
        if (!punctuality.equals(" "))
            event.setPunctuality(punctuality);
    }

    @Override
    public void collarDone(String collar) {
        event.setCollar(collar);
        result = result + " " + event.getPunctuality() + " " + collar;
        timeStart.setText(result);
    }

    @Override
    public void start(@NotNull Semester chosenSemester) {
        semester.setText(chosenSemester.getLong());
        App.setChosenSemester(chosenSemester);
    }
}
