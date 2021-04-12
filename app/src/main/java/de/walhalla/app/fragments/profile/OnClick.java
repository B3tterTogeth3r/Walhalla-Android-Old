package de.walhalla.app.fragments.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.dialog.ChangeSemesterDialog;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.models.Rank;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;

import static de.walhalla.app.fragments.profile.Fragment.context;
import static de.walhalla.app.fragments.profile.Fragment.dobTV;
import static de.walhalla.app.fragments.profile.Fragment.joinedTV;
import static de.walhalla.app.fragments.profile.Fragment.rankTV;
import static de.walhalla.app.fragments.profile.Fragment.userData;

public class OnClick implements View.OnClickListener {
    private static final String TAG = "ProfileOnClick";
    private FragmentManager fragmentManager;

    public OnClick(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.profile_general_information_name:
                DialogChange.display("name");
                break;
            case R.id.profile_general_information_DoB:
                selectBirthday().show();
                break;
            case R.id.profile_general_information_PoB:
                DialogChange.display("pob");
                break;
            case R.id.profile_general_information_major:
                DialogChange.display("major");
                break;
            case R.id.profile_general_information_password:
                Toast.makeText(context, App.getContext().getString(R.string.toast_still_in_dev), Toast.LENGTH_SHORT).show();
                //DialogChange.display("password", fragmentManager);
                break;
            case R.id.profile_contact_information_mail:
                Toast.makeText(context, App.getContext().getString(R.string.toast_still_in_dev), Toast.LENGTH_SHORT).show();
                //DialogChange.display("mail",fragmentManager);
                break;
            case R.id.profile_contact_information_mobile:
                DialogChange.display("mobile");
                break;
            case R.id.profile_contact_information_address:
                DialogChange.display("address");
                break;
            case R.id.profile_fraternity_information_rank:
                downloadRank();
                break;
            case R.id.profile_fraternity_information_joined:
                //Open joined selector
                selectJoinedSemester().show(fragmentManager, TAG);
                break;
            case R.id.profile_fraternity_information_picture:
                //TODO make that different so a picture can be uploaded.
                Toast.makeText(context, App.getContext().getString(R.string.toast_still_in_dev), Toast.LENGTH_SHORT).show();
                //DialogChange.display("picture", fragmentManager);
        }
    }

    /**
     * Download the ranks from the database. After that format them
     * into an ArrayList and display the dialog for the user to select one.
     */
    private void downloadRank() {
        Variables.Firebase.FIRESTORE
                .collection("Kind")
                .document("rank")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> dataSet = documentSnapshot.getData();
                        if (dataSet != null) {
                            int size = dataSet.size();
                            ArrayList<Rank> ranks = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                try {
                                    Map<String, Object> data = (Map<String, Object>) dataSet.get(String.valueOf(i));
                                    Rank r = new Rank();
                                    r.setId(i);
                                    r.setFirst_fraternity((boolean) data.get(Rank.FIRST_FRATERNITY));
                                    r.setFull_member((boolean) data.get(Rank.FULL_MEMBER));
                                    r.setIn_loco((boolean) data.get(Rank.IN_LOCO));
                                    r.setPrice_semester((Map<String, Object>) data.get(Rank.PRICE));
                                    r.setRank_name((String) data.get(Rank.NAME));
                                    ranks.add(r);
                                } catch (Exception e) {
                                    Log.d(TAG, "Something went wrong", e);
                                }
                            }
                            rankDialog(ranks)
                                    //.setOnDismissListener()
                                    .show();
                        } else {
                            Log.d(TAG, "2 something went wrong while downloading the ranks");
                        }
                    } else {
                        Log.d(TAG, "1 something went wrong while downloading the ranks");
                    }
                });
    }

    /**
     * Display the ranks a user can choose from
     *
     * @return The finished Builder.
     */
    @NotNull
    private AlertDialog.Builder rankDialog(@NotNull ArrayList<Rank> ranks) {
        String[] options = new String[ranks.size()];
        for (int i = 0; i < ranks.size(); i++) {
            options[i] = ranks.get(i).getRank_name();
        }
        AlertDialog.Builder rankPicker = new AlertDialog.Builder(context);
        rankPicker.setTitle(R.string.profile_choose_rank)
                .setNegativeButton(R.string.abort, (dialog, which) -> {
                })
                .setItems(options, (dialog, which) -> {
                    rankTV.setText(options[which]);
                    Log.i(TAG, which + " is the selected value");
                    userData.setRank(options[which]);
                });
        return rankPicker;
    }

    /**
     * The user can select its date of birth.
     *
     * @return The configured dialog to display.
     */
    @NotNull
    @Contract(" -> new")
    private DatePickerDialog selectBirthday() {
        Date date;
        try {
            date = userData.getDoB().toDate();
        } catch (Exception e) {
            date = new Date();
        }
        int[] DoB = new int[3];
        Locale locale = new Locale("de", "DE");
        SimpleDateFormat getYear = new SimpleDateFormat("yyyy", locale);
        SimpleDateFormat getMonth = new SimpleDateFormat("MM", locale);
        SimpleDateFormat getDay = new SimpleDateFormat("dd", locale);
        DoB[0] = Integer.parseInt(getYear.format(date));
        DoB[1] = Integer.parseInt(getMonth.format(date)) - 1;
        DoB[2] = Integer.parseInt(getDay.format(date));

        return new DatePickerDialog(context,
                (view1, year, monthOfYear, dayOfMonth) -> {
                    String month = String.valueOf(monthOfYear + 1);
                    if (Integer.parseInt(month) < 10) {
                        month = "0" + month;
                    }
                    String day = String.valueOf(dayOfMonth);
                    if (Integer.parseInt(day) < 10) {
                        day = "0" + day;
                    }
                    String result = day + "." + month + "." + year;
                    dobTV.setText(result);
                    Calendar c = Calendar.getInstance();
                    c.set(year, monthOfYear, dayOfMonth);
                    userData.setDoB(new Timestamp(c.getTime()));
                }, DoB[0], DoB[1], DoB[2]);
    }

    /**
     * The user can select the a semester.
     *
     * @return The configured dialog to display.
     */
    @NotNull
    @Contract(" -> new")
    private ChangeSemesterDialog selectJoinedSemester() {
        return new ChangeSemesterDialog(new ChosenSemesterListener() {
            @Override
            public void joinedSemesterDone(@NotNull Semester chosenSemester) {
                joinedTV.setText(chosenSemester.getLong());
                userData.setJoined(chosenSemester.getID());
            }

            @Override
            public void start(@NotNull Semester chosenSemester) {
                joinedTV.setText(chosenSemester.getLong());
                userData.setJoined(chosenSemester.getID());
            }
        }, userData.getJoined());
    }

}
