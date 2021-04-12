package de.walhalla.app.fragments.addNew;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.MainActivity;
import de.walhalla.app.R;
import de.walhalla.app.dialog.AccountPlanningDialog;
import de.walhalla.app.dialog.ErrorDialog;
import de.walhalla.app.dialog.PunctualityDialog;
import de.walhalla.app.interfaces.AddNewSemesterListener;
import de.walhalla.app.interfaces.CouleurTimePickerListener;
import de.walhalla.app.interfaces.NumberPickerCompleteListener;
import de.walhalla.app.models.Event;
import de.walhalla.app.utils.Variables;

public class EventsDialog extends DialogFragment implements OnMapReadyCallback, View.OnClickListener,
        CouleurTimePickerListener, NumberPickerCompleteListener {
    private static final String TAG = "ChargenDialog";
    protected static Marker marker;
    private static Event event = new Event();
    private static String whichOne = Variables.ADD;
    private static FragmentManager manager;
    private final AddNewSemesterListener addNewSemesterListener;
    private EditText title, description;
    private Button begin, end, planning;
    private CheckBox meeting, place;
    private Toolbar toolbar;
    private boolean isMeeting = false, startAdh = true;
    private LinearLayout linearLayout;
    private FrameLayout frameLayout;
    private ArrayList<Event> eventList = new ArrayList<>();
    private String result;
    private Map<String, Object> accountingElements = new HashMap<>();
    private TextView accBefore, accAfter;
    private ListenerRegistration registration;

    public EventsDialog(AddNewSemesterListener addNewSemesterListener) {
        this.addNewSemesterListener = addNewSemesterListener;
    }

    public static void display(FragmentManager fragmentManager, @Nullable Event event, @NotNull AddNewSemesterListener addNewSemesterListener) {
        EventsDialog.manager = fragmentManager;
        if (event != null) {
            EventsDialog.event = event;
            EventsDialog.whichOne = Variables.EDIT;
        }
        try {
            EventsDialog dialog = new EventsDialog(addNewSemesterListener);
            dialog.show(fragmentManager, TAG);
        } catch (Exception e) {
            Snackbar.make(MainActivity.parentLayout, R.string.error_display, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog DIALOG = getDialog();
        if (DIALOG != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(DIALOG.getWindow()).setLayout(width, height);
            DIALOG.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
        registration = Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document("accounting")
                .addSnapshotListener(MetadataChanges.INCLUDE, ((value, error) -> {
                    if (error != null) {
                        Log.d(TAG, "listener:error", error);
                        return;
                    }
                    accountingElements.clear();
                    if (value != null && value.exists()) {
                        try {
                            accountingElements = value.getData();
                        } catch (Exception ignored) {
                        }
                    }
                }));
    }

    @Override
    public void onStop() {
        try {
            registration.remove();
        } catch (Exception ignored) {
        }
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.program_edit, container, false);
        //Initialize all the variables which have to respond with the ui
        linearLayout = view.findViewById(R.id.program_details_layout);
        view.findViewById(R.id.program_semester).setVisibility(View.GONE);
        view.findViewById(R.id.program_edit_tasks).setVisibility(View.GONE);
        view.findViewById(R.id.program_edit_acc_current_row).setVisibility(View.GONE);
        begin = view.findViewById(R.id.program_edit_start_time);
        end = view.findViewById(R.id.program_edit_end_time);
        planning = view.findViewById(R.id.program_edit_accounting);
        meeting = view.findViewById(R.id.program_meeting);
        place = view.findViewById(R.id.program_start_place);
        title = view.findViewById(R.id.program_edit_title);
        description = view.findViewById(R.id.program_edit_description);
        accBefore = view.findViewById(R.id.program_edit_acc_before);
        accAfter = view.findViewById(R.id.program_edit_acc_after);

        toolbar = view.findViewById(R.id.program_details_close);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do stuff
        begin.setOnClickListener(this);
        end.setOnClickListener(this);
        planning.setOnClickListener(this);
        meeting.setOnClickListener(this);
        place.setOnClickListener(this);

        if (whichOne.equals(Variables.ADD)) {
            place.setOnClickListener(this);
        } else {
            place.setEnabled(false);
            place.setClickable(false);
        }

        toolbar.setTitle(R.string.program_add);
        toolbar.setNavigationOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.attention)
                    .setMessage(R.string.abort_sure)
                    .setCancelable(true)
                    .setNeutralButton(R.string.abort, null)
                    .setPositiveButton(R.string.yes, ((dialog, which) -> this.dismiss()));
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });
        //add new menu with next and done
        toolbar.inflateMenu(R.menu.program_next_end);
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "Menu item clicked: " + item.getTitle().toString());
            //check for valid data input before adding the event
            boolean allNecessaryData = true;
            Toast toast = Toast.makeText(App.getContext(), R.string.program_new_saved, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            if (event.getStart() == null ||
                    event.getEnd() == null ||
                    event.getTitle().isEmpty()) {
                allNecessaryData = false;
            }
            if (allNecessaryData && item.getItemId() == R.id.action_next) {
                //save element to list AND [open dialog again OR reset it]
                this.eventList.add(event);
                EventsDialog dialog = new EventsDialog(addNewSemesterListener);
                dialog.setEventList(eventList);
                dialog.show(manager, TAG);
                toast.show();
                this.dismiss();
                return true;
            } else if (allNecessaryData && item.getItemId() == R.id.action_done) {
                //save element to list and go to custom listener in NewSemesterDialog
                try {
                    this.eventList.add(event);
                    Log.d(TAG, eventList.size() + "");
                    addNewSemesterListener.eventsDone(eventList);
                    int size = eventList.size();
                    if (size == 0 || size == 1) {
                        toast.setText("Es wurde eine Veranstaltung erstellt.");
                    } else {
                        toast.setText("Es wurden " + eventList.size() + " Veranstaltungen erstellt.");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "the expected error occurred", e);
                }
                toast.show();
                this.dismiss();
                return true;
            } else {
                Snackbar.make(requireView(), "Es kann keine leere Veranstaltung erstellt werden", Snackbar.LENGTH_LONG).show();
                return false;
            }
        });
        frameLayout = new FrameLayout(requireContext());
        frameLayout.setId(R.id.maps_fragment);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Variables.SCALE * 400);
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

        //Save changes in title and description immediately
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                event.setTitle(s.toString());
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                event.setDescription(s.toString());
            }
        });
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
            if (startAdh && whichOne.equals(Variables.ADD)) {
                frameLayout.setVisibility(View.GONE);
            } else {
                frameLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.d(TAG, "Visibility did not work on the Map", e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == meeting) {
            isMeeting = !isMeeting;
            Log.d(TAG, "Is event a meeting: " + isMeeting);
        } else if (v == place) {
            startAdh = !startAdh;
            Log.d(TAG, "Does event start adh: " + startAdh);
            if (startAdh && whichOne.equals(Variables.ADD)) {
                frameLayout.setVisibility(View.GONE);
            } else {
                frameLayout.setVisibility(View.VISIBLE);
            }
        } else if (v == begin) {
            Log.d(TAG, begin.getText().toString());
            date(Event.START);
        } else if (v == end) {
            Log.d(TAG, end.getText().toString());
            date(Event.END);
        } else if (v == planning) {
            Log.d(TAG, planning.getText().toString());
            accounting();
        } else if (v == toolbar) {
            this.dismiss();
        } else {
            Log.d(TAG, "An unassigned button got clicked" + v.getId());
        }
    }

    /**
     * Download the possible ways of accounting and display a numberPicker dialog
     * with them inside. The user chooses one and the result gets saved in the event
     */
    private void accounting() {
        if (accountingElements.size() > 0) {
            try {
                List<String> names = new ArrayList<>(accountingElements.keySet());
                Collections.sort(names);
                if (names.size() != 0) {
                    new AccountPlanningDialog(getContext(), names, EventsDialog.this).show();
                } else {
                    Log.d(TAG, "names.size ==0");
                }
            } catch (Exception ignored) {
            }
        }
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
                        begin.setText(R.string.program_edit_time_start);
                        event.setStart(new Timestamp(c.getTime()));
                    } else if (kind.equals(Event.END)) {
                        end.setText(R.string.program_dialog_time_end);
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
                            end.setText(result);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
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
        begin.setText(result);
    }

    @Override
    public void notifyOfAccountingDone(String name) {
        Map<String, Object> data = (Map<String, Object>) accountingElements.get(name);
        String format = "€ " + data.get("before").toString() + ",00";
        accBefore.setText(format);
        format = "€ " + data.get("after").toString() + ",00";
        accAfter.setText(format);
        format = getString(R.string.program_dialog_accounting) + ": " + name;
        planning.setText(format);
        event.setBudget(data);
    }

    @Override
    public void notifyOfTaskDone(@NotNull Map<String, Object> helpers) {

    }
}
