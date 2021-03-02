package de.walhalla.app.fragments.program.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.dialog.CouleurTimePickerDialog;
import de.walhalla.app.dialog.ErrorDialog;
import de.walhalla.app.dialog.NumberPickerDialog;
import de.walhalla.app.interfaces.CouleurTimePickerListener;
import de.walhalla.app.interfaces.NumberpickerCompleteListener;
import de.walhalla.app.models.AllEvents;
import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.utils.Find;
import de.walhalla.app.utils.Variables;

@SuppressLint("StaticFieldLeak")
public class Edit extends DialogFragment implements OnMapReadyCallback, View.OnClickListener,
        CouleurTimePickerListener, NumberpickerCompleteListener {
    protected static final float scale = App.getContext().getResources().getDisplayMetrics().density;
    private static final String TAG = "Details of Program-Event";
    protected static Marker marker;
    private final Event event;
    private Toolbar toolbar;
    private EditText title, description;
    private Button timeStart, timeEnd, accounting;
    private TextView accBefore, accAfter;
    private String result, whichOne;

    public Edit(Event event) {
        //Edit an existing event
        this.event = event;
        this.whichOne = Variables.EDIT;
    }

    public Edit() {
        //Add a new Event
        this.whichOne = Variables.ADD;
        Calendar c = Calendar.getInstance();
        this.event = new Event();
    }

    public static void display(FragmentManager fragmentManager, @Nullable Event event) {
        Edit editDialog;
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


        LinearLayout linearLayout = view.findViewById(R.id.program_details_layout);
        toolbar = view.findViewById(R.id.program_details_close);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.program_details_layout);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.program_details);
        toolbar.inflateMenu(R.menu.send_only);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send) {
                Log.i(TAG, "Send got clicked");
                send(whichOne);
            } else {
                dismiss();
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

        timeStart.setOnClickListener(this);
        timeEnd.setOnClickListener(this);
        accounting.setOnClickListener(this);

        FrameLayout frameLayout = new FrameLayout(requireContext());
        frameLayout.setId(R.id.maps_fragment);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scale * 400);
        linearLayout.addView(frameLayout, params);

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
                c.get(Calendar.YEAR) + " " + hour + ":" + minute +
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
                c.get(Calendar.YEAR) + " " + hour + ":" + minute +
                getResources().getString(R.string.clock);
        timeEnd.setText(helper);

        //TODO accounting.setText(event.getPlan());
        //TODO Add the two checkboxes
/*
            ArrayList<AllEvents> eventsArrayList = Database.getAllEventsArrayList();
            new AllEvents();
            AllEvents allEvents;
            for (int i = 0; i < eventsArrayList.size(); i++) {
                if (eventsArrayList.get(i).getName().equals(event.getPlan())) {
                    allEvents = eventsArrayList.get(i);
                    float helperFL = (float) allEvents.getBefore();
                    helper = "€ " + String.format(Variables.LOCALE, "%,2f", helperFL);
                    accBefore.setText(helper);
                    helperFL = (float) allEvents.getAfter();
                    helper = "€ " + String.format(Variables.LOCALE, "%,2f", helperFL);
                    accAfter.setText(helper);
                }
            }

            /* Bottom with Helper */
        if (User.isLogIn()) {
            ArrayList<Helper> helperArrayList = new ArrayList<>();//TODO Find.help4event(event);
            if (!helperArrayList.isEmpty()) {
                LinearLayout planingLayout = new LinearLayout(getContext());
                planingLayout.setOrientation(LinearLayout.VERTICAL);
                TextView planingTitle = new TextView(getContext());
                planingTitle.setText(R.string.program_button_given_tasks);
                planingLayout.addView(planingTitle);
                ArrayList<ArrayList<Helper>> task = Find.tasks(helperArrayList);
                for (int i = 0; i < task.size(); i++) {
                    ArrayList<Helper> work = task.get(i);
                    if (!work.isEmpty()) {
                        LinearLayout lv2 = new LinearLayout(getContext());
                        lv2.setOrientation(LinearLayout.HORIZONTAL);
                        TextView job = new TextView(getContext());
                        helper = work.get(0).getJob() + ": ";
                        job.setText(helper);
                        lv2.addView(job);
                        for (Helper p : work) {
                            TextView person = new TextView(getContext());
                            person.setText(p.getPersonClean().getFullName());
                            lv2.addView(person);
                        }
                        planingLayout.addView(lv2);
                    }
                }
                linearLayout.addView(planingLayout);
            }
        }

    }

    private void send(String whichOne) {

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
            e.printStackTrace();
        }
        int index = 0;
        if (!whichOne.equals(Variables.DELETE)) {
            Event upload = new Event();
            boolean error = false;
            if (whichOne.equals(Variables.ADD)) {

                //Upload toUpload to the online database and add it to the local ArrayList
                try {
                    //TODO index = Database.getEventArrayList().get(Database.getEventArrayList().size() - 1).getId();
                    //event.setId(index);
                    if (event.getStart() == null ||
                            event.getEnd() == null ||
                            //TODO event.getPunkt() == null ||
                            event.getCollar() == null ||
                            event.getTitle() == null) {
                        error = true;
                    }
                    if (event.getLocation_name() == null)
                        event.setLocation_name(Variables.Walhalla.NAME);
                    if (event.getLocation_coordinates() == null)
                        event.setLocation_coordinates(Variables.Walhalla.ADH_LOCATION);
                    upload = event;
                } catch (Exception e) {
                    uploadError();
                }
                //Upload them to the firebase realtime database
            }
            if (whichOne.equals(Variables.EDIT)) {
                //TODO index = event.getId();
                upload = event;
                //Upload them to the firebase realtime database
            }
            if (!error) {
                /*Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("start").setValue(upload.getStart());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("end").setValue(upload.getEnd());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("punkt").setValue(upload.getPunkt());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("collar").setValue(upload.getCollar());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("title").setValue(upload.getTitle());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("description").setValue(upload.getDescription());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("plan").setValue(upload.getPlan());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("locationName").setValue(upload.getLocationName());
                Variables.Firebase.Reference.EVENT.child(String.valueOf(index)).child("locationCoordinates").setValue(upload.getLocationCoordinatesString())
                        .addOnFailureListener(e -> uploadError())
                        .addOnSuccessListener(aVoid -> {
                            Snackbar.make(MainActivity.parentLayout, R.string.upload_complete, Snackbar.LENGTH_LONG).show();
                            Details.DIALOG.dismiss();
                            dismiss();
                        });*/
            } else {
                uploadError();
            }
        } else {
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == timeStart) {
            date(Event.START);
        } else if (v == timeEnd) {
            date(Event.END);
        } else if (v == accounting) {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(getContext(), "ProgramDialog", this);
            numberPickerDialog.show();
        }
    }

    protected void date(String kind) {
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
                    result = day + "." + Variables.MONTHS[monthOfYear] + " " + year;
                    if (kind.equals(Event.START)) {
                        timeStart.setText(result);
                        event.setStart(new Timestamp(c.getTime()));
                    } else if (kind.equals(Event.END)) {
                        timeEnd.setText(result);
                        event.setEnd(new Timestamp(c.getTime()));
                    }
                    time(kind);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    protected void time(String kind) {
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
                        result = timeStart.getText() + " " + resultTime;
                        //Open picker for none/ct/st, io/o/ho and "none/anschließend/ganztägig/gleicher Tag/Info"
                        Dialog dialog = new CouleurTimePickerDialog(requireContext(), this);
                        dialog.show();
                        dialog.setOnDismissListener(dialog1 -> timeStart.setText(result));
                    } else if (kind.equals(Event.END)) {
                        //Test for same or later date as start
                        if (c.getTime().before(event.getStart().toDate())) {
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
                            result = timeEnd.getText() + " " + resultTime;
                            timeEnd.setText(result);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

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
    }

    private void uploadError() {
        Snackbar.make(requireView(), R.string.error_upload, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, v -> {
                })
                .setActionTextColor(App.getContext().getResources().getColor(R.color.colorPrimaryDark, null))
                .show();
    }

    @Override
    public void notifyOfDialogDone(String punctuality, @NotNull String dayTime, String collar) {
        result = result + " " + punctuality + " " + collar + " " + dayTime;
        event.setCollar(collar);
        //Translate punctuality
        switch (dayTime) {
            case ("anschließend"):
                dayTime = "after";
                punctuality = "";
                break;
            case ("ganztägig"):
                dayTime = "total";
                punctuality = "";
                break;
            case ("gleicher Tag"):
                dayTime = "later ";
                break;
            case ("Info"):
                dayTime = "info";
                punctuality = "";
                break;
            case ("noch unbekannt"):
                dayTime = "unknown ";
                break;
            default:
                dayTime = "";
        }
        //TODO event.setPunkt(dayTime + punctuality);
    }

    @Override
    public void notifyOfNumberPickerDone(@NotNull AllEvents allEvents) {
        accounting.setText(allEvents.getName());
        String before = "€ " + allEvents.getBefore() + ",00";
        accBefore.setText(before);
        String after = "€ " + allEvents.getAfter() + ",00";
        accAfter.setText(after);
        //TODO event.setPlan(allEvents.getName());
    }
}
