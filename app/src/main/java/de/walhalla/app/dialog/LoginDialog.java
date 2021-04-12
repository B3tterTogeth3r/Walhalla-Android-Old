package de.walhalla.app.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.interfaces.ChosenSemesterListener;
import de.walhalla.app.interfaces.PasswordCheck;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Rank;
import de.walhalla.app.models.Semester;
import de.walhalla.app.utils.Variables;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

@SuppressLint("StaticFieldLeak")
public class LoginDialog extends DialogFragment implements View.OnClickListener, PasswordCheck {
    private static final String TAG = "LoginDialog";
    private static EditText password, password_control;
    private final PasswordCheck listener;
    private final boolean[] check = {false, false};
    private View view;
    private Toolbar toolbar;
    private EditText email, firstName, lastName, address, pob, mobile, zip, number, city, street, major;
    private LinearLayout loginLayout, signInLayout, signUpLayout, settingsLayout;
    private ScrollView signUpDetailsLayout;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch first_frat, fill_member, in_loco, get_mail, get_post, get_push;
    private Button dob, joined, rank, signUp;
    private String emailStr, passwordStr;
    private Person save;
    private ImageView control_top, control_bottom;
    private Drawable allGood, error;

    public LoginDialog() {
        listener = this;
    }

    public static void display(FragmentManager fragmentManager) {
        try {
            LoginDialog dialog = new LoginDialog();
            dialog.show(fragmentManager, TAG);
        } catch (Exception ignored) {
        }
    }

    /**
     * Check the password for an upper case letter, a lower case letter and a number.
     *
     * @param str The input value.
     * @return False if not all three criteria are met
     */
    private static boolean checkPw(@NotNull String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog_login, container, false);

        loginLayout = view.findViewById(R.id.login_layout);
        signInLayout = view.findViewById(R.id.login_sign_in_layout);
        signUpLayout = view.findViewById(R.id.login_sign_up_layout_auth);
        signUpDetailsLayout = view.findViewById(R.id.login_profile);
        settingsLayout = view.findViewById(R.id.profile_settings);
        RelativeLayout profile_bottom_buttons = view.findViewById(R.id.profile_bottom_buttons);

        loginLayout.setVisibility(View.VISIBLE);
        signInLayout.setVisibility(View.GONE);
        signUpLayout.setVisibility(View.GONE);
        signUpDetailsLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        profile_bottom_buttons.setVisibility(View.VISIBLE);

        toolbar = view.findViewById(R.id.program_details_close);

        return view;
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        save = new Person();

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
        toolbar.setTitle(R.string.login_sign_in);
        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.login_password);

        Button next = view.findViewById(R.id.login_next);
        Button signIn = view.findViewById(R.id.login_sign_in);
        Button signInBack = view.findViewById(R.id.login_sign_in_back);
        signUp = view.findViewById(R.id.login_sign_up);
        Button signUpBack = view.findViewById(R.id.login_sign_up_back);
        Button register = view.findViewById(R.id.profile_send);
        Button registerBack = view.findViewById(R.id.profile_send_back);
        control_top = view.findViewById(R.id.login_sign_up_control_top);
        control_bottom = view.findViewById(R.id.login_sign_up_control_bottom);

        firstName = view.findViewById(R.id.profile_firstName);
        lastName = view.findViewById(R.id.profile_lastName);
        address = view.findViewById(R.id.profile_address);
        street = view.findViewById(R.id.profile_address_street);
        number = view.findViewById(R.id.profile_address_number);
        zip = view.findViewById(R.id.profile_address_zip);
        city = view.findViewById(R.id.profile_address_city);
        pob = view.findViewById(R.id.profile_pob);
        major = view.findViewById(R.id.profile_major);
        mobile = view.findViewById(R.id.profile_mobile);
        dob = view.findViewById(R.id.profile_dob);
        joined = view.findViewById(R.id.profile_joined);
        rank = view.findViewById(R.id.profile_rank);
        first_frat = view.findViewById(R.id.profile_first_fraternity);
        fill_member = view.findViewById(R.id.profile_full_member);
        in_loco = view.findViewById(R.id.profile_in_loco);
        get_mail = view.findViewById(R.id.profile_get_mail);
        get_post = view.findViewById(R.id.profile_get_post);
        get_push = view.findViewById(R.id.profile_get_push);

        next.setOnClickListener(this);
        signIn.setOnClickListener(this);
        signInBack.setOnClickListener(this);
        signUp.setOnClickListener(this);
        signUpBack.setOnClickListener(this);
        register.setOnClickListener(this);
        registerBack.setOnClickListener(this);
        dob.setOnClickListener(this);
        joined.setOnClickListener(this);
        rank.setOnClickListener(this);

        allGood = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_task_alt)));
        DrawableCompat.setTint(allGood, Color.GREEN);

        error = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_error_outline)));
        DrawableCompat.setTint(error, Color.RED);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NotNull View v) {
        //hide keyboard
        try {
            InputMethodManager im = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert im != null;
            im.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //First site
        if (v.getId() == R.id.login_next) {
            loginNext();
        }
        //E-Mail exists in Firebase Auth, User to enter password
        else if (v.getId() == R.id.login_sign_in) {
            if (TextUtils.isEmpty(password.getText().toString())) {
                Log.e(TAG, "password is empty -> doing nothing");
            } else {
                signIn(email.getText().toString(), password.getText().toString());
            }
        }
        //E-Mail does not exist in Firebase Auth, user to register
        else if (v.getId() == R.id.login_sign_up) {
            signIn();
        }
        //User to complete register
        else if (v.getId() == R.id.profile_send) {
            profileSend();
        }
        //User to select its date of birth
        else if (v.getId() == R.id.profile_dob) {
            //Open Date selector
            selectBirthday().show();

        }
        //User to select the semester it joined the fraternity
        else if (v.getId() == R.id.profile_joined) {
            //Open joined selector
            ChangeSemesterDialog joinedSemDialog = new ChangeSemesterDialog(new ChosenSemesterListener() {
                @Override
                public void joinedSemesterDone(@NotNull Semester chosenSemester) {
                    joined.setText(chosenSemester.getLong());
                    save.setJoined(chosenSemester.getID());
                }

                @Override
                public void start(@NotNull Semester chosenSemester) {
                    joined.setText(chosenSemester.getLong());
                    save.setJoined(chosenSemester.getID());
                }
            }, save.getJoined());
            joinedSemDialog.show(getParentFragmentManager(), null);

        }
        //User to select its current rank
        else if (v.getId() == R.id.profile_rank) {
            downloadRank();
        }
        //User clicked the back button while on sign up site
        else if (v.getId() == R.id.login_sign_up_back) {
            loginLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.GONE);
            signUpDetailsLayout.setVisibility(View.GONE);
            try {
                email.setText("");
                password.setText("");
                password_control.setText("");
                emailStr = "";
            } catch (Exception ignored) {
            }
        }
        //User clicked back button while on register site
        else if (v.getId() == R.id.profile_send_back) {
            loginLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.VISIBLE);
            signUpDetailsLayout.setVisibility(View.GONE);
            try {
                email.setText(emailStr);
            } catch (Exception ignored) {
            }
        }
        //User clicked back while on log in site
        else if (v.getId() == R.id.login_sign_in_back) {
            loginLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.GONE);
            signUpDetailsLayout.setVisibility(View.GONE);
            try {
                email.setText("");
                emailStr = "";
            } catch (Exception ignored) {
            }
        }
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
            date = save.getDoB().toDate();
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

        return new DatePickerDialog(requireContext(),
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
                    dob.setText(result);
                    result = year + "-" + month + "-" + day;
                    Log.i(TAG, result);
                    Calendar c = Calendar.getInstance();
                    c.set(year, monthOfYear, dayOfMonth);
                    save.setDoB(new Timestamp(c.getTime()));
                }, DoB[0], DoB[1], DoB[2]);
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
                                    .setOnDismissListener(dialog -> {
                                        try {
                                            String rank;
                                            rank = save.getRank();

                                            if (rank.contains("Alter")) {
                                                view.findViewById(R.id.profile_in_loco_layout).setVisibility(View.GONE);
                                                in_loco.setChecked(true);
                                            }
                                            settingsLayout.setVisibility(View.VISIBLE);
                                        } catch (Exception ignored) {
                                        }
                                    })
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
        AlertDialog.Builder rankPicker = new AlertDialog.Builder(getContext());
        rankPicker.setTitle(R.string.profile_choose_rank)
                .setNegativeButton(R.string.abort, (dialog, which) -> {
                })
                .setItems(options, (dialog, which) -> {
                    rank.setText(options[which]);
                    Log.i(TAG, which + " is the selected value");
                    save.setRank(options[which]);
                });
        return rankPicker;
    }

    /**
     * Check if the the input at email field
     * is already signed up. If the user is not available
     * the UI will display two fields for the password.
     * If the user is available, ui will show a welcome
     * back message and one field for the user to put
     * its password in.
     */
    private void loginNext() {
        if (TextUtils.isEmpty(email.getText().toString())) {
            Log.e(TAG, "email is empty -> doing nothing");
        } else {
            signIn(email.getText().toString(), "Test1234");
        }
    }

    /**
     * Check for the necessary data to register a new person.
     *
     * @param save All the data the user wrote.
     * @return true if the fields are not empty, else false.
     */
    private boolean controlUserInput(@NotNull Person save) {
        boolean first = (save.getFirst_Name() != null && !save.getFirst_Name().equals(""));
        boolean last = (save.getLast_Name() != null && !save.getLast_Name().equals(""));
        boolean mail = (save.getMail() != null && !save.getMail().equals(""));
        boolean mobile = (save.getMobile() != null && !save.getMobile().equals(""));
        boolean rank = (save.getRank() != null && !save.getRank().equals(""));
        boolean address = (!save.getAddress().get(Person.ADDRESS_CITY).toString().equals("") &&
                !save.getAddress().get(Person.ADDRESS_ZIP_CODE).toString().equals("") &&
                !save.getAddress().get(Person.ADDRESS_NUMBER).toString().equals("") &&
                !save.getAddress().get(Person.ADDRESS_STREET).toString().equals(""));
        boolean joined = (save.getJoined() != 0);
        boolean settings = (save.getRankSettings() != null && save.getRankSettings().size() != 0);
        boolean major = (save.getMajor() != null && !save.getMajor().equals(""));
        return (first && last && mail && mobile && rank && address && joined && settings && major);
    }

    /**
     * Check with the online Firebase Auth if the combination of email and password is valid.
     * There are 3 caught possible outcomes: <br>
     * First: <br>&nbsp;&nbsp;The mail and password combination is valid<br>&nbsp;&nbsp;&nbsp;&nbsp;-> the user gets signed in <br>
     * Second: <br>&nbsp;&nbsp;The mail is not in the Auth database<br>&nbsp;&nbsp;-> the user gets referred to the register page <br>
     * Third: <br>&nbsp;&nbsp;The mail and user combination is not valid<br>&nbsp;&nbsp;-> an error message gets displayed <br>
     * Else: <br>&nbsp;&nbsp;A debuggable message gets send to the console.
     *
     * @param email    The Email the user wrote into the EditText or
     *                 the one that got saved from a previous input
     * @param password The password the user put into the EditText or
     *                 the one that got saved from the register page or
     *                 just a dummy string to check for a valid email
     */
    private void signIn(String email, String password) {
        Log.i(TAG, "signIn: " + email);

        //Variables.Firebase.AUTHENTICATION.addAuthStateListener(new CustomAuthListener());

        Variables.Firebase.AUTHENTICATION.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    //Email with that password is existing.
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = Variables.Firebase.AUTHENTICATION.getCurrentUser();
                        if (user != null) {
                            //User.setData(save, user.getUid());
                            //MainActivity.authChange.onAuthChange();
                            //CustomFragment.authChange.onAuthChange();
                            //Find.PersonByUID(user.getUid(), email);
                            Log.d("Test", User.getData().getFullName());
                        }
                        dismiss();
                    }
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthInvalidUserException invalidEmail) {
                            Log.d(TAG, "onComplete: invalid_email");
                            setViewForRegister(email);
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                            Log.d(TAG, "onComplete: wrong_password");
                            //go to log in (sign in)
                            loginLayout.setVisibility(View.GONE);
                            signInLayout.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * Sets up the View to display two inputs for a password.
     * the password and password_control inputs are made visible
     * thy also get a TextWatcher to check for certain security rules
     *
     * @param email the previous input is displayed
     */
    private void setViewForRegister(String email) {
        loginLayout.setVisibility(View.GONE);
        toolbar.setTitle(R.string.menu_register);
        signUpLayout.setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.login_sign_up_email)).setText(email);
        emailStr = email;

        LoginDialog.password = view.findViewById(R.id.login_sign_up_password);
        LoginDialog.password_control = view.findViewById(R.id.login_sign_up_password_control);

        control_bottom.setVisibility(View.INVISIBLE); //For an error message it invisible until a check was made
        control_top.setVisibility(View.INVISIBLE); //For an error message it invisible until a check was made
        LoginDialog.password_control.addTextChangedListener(passwordController());
        LoginDialog.password.addTextChangedListener(passwordSecurity());
    }

    /**
     * The listener is only "happy" if some rules are true.<br>
     * First: The input value has to pass the checkPw() function<br>
     * Second: The input value has to be at least 8 digits long<br>
     * If both are true, the error symbol will not be checked anymore
     * and the listener for the send button will be activated<br>
     * <p>Otherwise the listener gets false and an error symbol will
     * appear behind the EditText. That symbol is clickable to
     * display its error message.</p>
     *
     * @return The TextWatcher to control the password security
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private TextWatcher passwordSecurity() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                control_top.setVisibility(View.VISIBLE);
                boolean checkString = checkPw(s.toString());
                if (8 <= s.length() && checkString) {
                    control_top.setImageDrawable(allGood);
                    control_top.setOnClickListener(null);
                    listener.send(0, true);
                } else {
                    listener.send(0, false);
                    control_top.setImageDrawable(error);
                    String message = "";
                    if (!checkString) {
                        message = getString(R.string.error_login_password_insecure);
                    }
                    if (s.length() < 8) {
                        if (message.length() != 0) {
                            message = message + "\n ";
                        }
                        message = message + getString(R.string.error_login_password_length);
                    }
                    String finalMessage = message;
                    control_top.setOnClickListener(v ->
                            new SimpleTooltip.Builder(requireContext())
                                    .anchorView(v)
                                    .text(finalMessage)
                                    .gravity(Gravity.TOP)
                                    .animated(false)
                                    .build()
                                    .show());
                }
            }
        };
    }

    /**
     * only if s equals the input of password the listener will be "happy"
     * otherwise an clickable error symbol will appear.
     * <p>If the first and second password match:<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Display an icon to represent no errors were found<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Disable the OnClickListener<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Send the confirmation to check for the other field<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;and save the password</p>
     * <p>While the to passwords don't match<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Set the confirmation listener to false<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Display an error symbol at the end of the TV<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Make the symbol clickable to display the error message</p>
     *
     * @return The TextWatcher to control the password control
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private TextWatcher passwordController() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                control_bottom.setVisibility(View.VISIBLE);
                if (LoginDialog.password.getText().toString().equals(LoginDialog.password_control.getText().toString())) {
                    control_bottom.setImageDrawable(allGood);
                    control_bottom.setOnClickListener(null);
                    listener.send(1, true);
                    passwordStr = s.toString();
                } else {
                    listener.send(1, false);
                    control_bottom.setImageDrawable(error);
                    control_bottom.setOnClickListener(v ->
                            new SimpleTooltip.Builder(requireContext())
                                    .anchorView(v)
                                    .text(R.string.error_login_password_not_same)
                                    .gravity(Gravity.TOP)
                                    .animated(true)
                                    .build()
                                    .show());
                }
            }
        };
    }

    /**
     * Displays a dialog with an error message.
     *
     * @param kind is used to determine which message to display
     */
    private void error(@NotNull String kind) {
        String message = "Something went terribly wrong. Try again later";
        switch (kind) {
            case "email":
                message = App.getContext().getString(R.string.error_login_email_empty);
                break;
            case "display_load":
                message = App.getContext().getString(R.string.error_display);
                break;
            case "password":
                message = App.getContext().getString(R.string.error_login_password_empty);
                break;
            case "credentials_mail":
                message = App.getContext().getString(R.string.error_login_email);
                break;
            case "credentials_password":
                message = App.getContext().getString(R.string.error_login_password);
                break;
            case "signInUnsuccessful":
                message = App.getContext().getString(R.string.error_login_unsuccessful);
                break;
            case "upload_error":
                message = App.getContext().getString(R.string.error_login_upload_unsuccessful);
                break;
            case "input_incomplete":
                message = App.getContext().getString(R.string.error_login_input_incomplete);
                break;
            case "passwords":
                message = App.getContext().getString(R.string.error_login_password_not_same);
                break;
            default:
                break;
        }
        Toast.makeText(App.getContext(), message,
                Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.error_title)
                .setMessage(message)
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void send(int number, boolean done) {
        check[number] = done;
        signUp.setClickable(check[0] && check[1]);
    }

    private void signIn() {
        if (!TextUtils.isEmpty(password.getText().toString()) | !TextUtils.isEmpty(password_control.getText().toString())) {
            if (password.getText().toString().equals(password_control.getText().toString())) {
                Variables.Firebase.FIRESTORE
                        .collection("Person")
                        .whereEqualTo("mail", emailStr)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    List<Person> p = task.getResult().toObjects(Person.class);
                                    profile_checked(p.get(0));
                                } else {
                                    profile_checked(null);
                                }
                            }
                        });
            } else {
                error("passwords");
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.error_title)
                    .setMessage(R.string.error_login_password_empty);
            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    private void profile_checked(@Nullable Person person) {
        signUpLayout.setVisibility(View.GONE);
        toolbar.setTitle(R.string.menu_register);

        signUpDetailsLayout.setVisibility(View.VISIBLE);
        if (person != null) {
            save = person;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.attention)
                    .setMessage(R.string.found_entry)
                    .setCancelable(true);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            try {
                firstName.setText(save.getFirst_Name());
                lastName.setText(save.getLast_Name());
                String addressStr;
                addressStr = person.getAddress().get(Person.ADDRESS_STREET).toString() + " " +
                        person.getAddress().get(Person.ADDRESS_NUMBER).toString() + "\n\r" +
                        person.getAddress().get(Person.ADDRESS_ZIP_CODE).toString() + " " +
                        person.getAddress().get(Person.ADDRESS_CITY).toString();
                address.setText(addressStr);
                dob.setText(save.getDoB().toDate().toString());
                pob.setText(save.getPoB());
                joined.setText(save.getJoined());
                mobile.setText(save.getMobile());
                rank.setText(save.getRank());
            } catch (Exception ignored) {
            }
        }
    }

    private void profileSend() {
        save.setFirst_Name(firstName.getText().toString());
        save.setLast_Name(lastName.getText().toString());
        Log.i(TAG, "register person " + save.getFullName());
        Map<String, Object> address = new HashMap<>();
        address.put(Person.ADDRESS_STREET, street.getText().toString());
        address.put(Person.ADDRESS_NUMBER, number.getText().toString());
        address.put(Person.ADDRESS_CITY, city.getText().toString());
        address.put(Person.ADDRESS_ZIP_CODE, zip.getText().toString());
        save.setMail(emailStr);
        save.setAddress(address);
        save.setMobile(mobile.getText().toString());
        save.setPoB(pob.getText().toString());
        save.setMajor(major.getText().toString());
        //DoB, Joined and Rank are done by a button
        //get switch states
        List<String> rankSettings = checkBooleans();
        save.setRankSettings(rankSettings);

        //check for correct data-input
        if (controlUserInput(save)) {
            Variables.Firebase.AUTHENTICATION.createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = Variables.Firebase.AUTHENTICATION.getCurrentUser();
                            if (user != null) {
                                save.setUid(user.getUid());
                                //upload data to firestore
                                uploadData(save);
                            }
                        }
                    });
        } else {
            Log.e(TAG, "error on send");
            error("input_incomplete");
        }
    }

    /**
     * This method checks all the available Switches and writes
     * the corresponding data into the array for Firestore.
     *
     * @return The resulting List gets returned.
     */
    @NotNull
    private List<String> checkBooleans() {
        List<String> rankSettings = new ArrayList<>();
        if (first_frat.isChecked()) {
            rankSettings.add("A");
        } else {
            rankSettings.add("B");
        }
        if (in_loco.isChecked()) {
            rankSettings.add("in_loco");
        } else {
            rankSettings.add("ex_loco");
        }
        if (fill_member.isChecked()) {
            rankSettings.add("fux");
        }
        if (get_mail.isChecked()) {
            rankSettings.add("getMail");
        }
        if (get_post.isChecked()) {
            rankSettings.add("getPost");
        }
        if (get_push.isChecked()) {
            rankSettings.add("getPush");
        }

        return rankSettings;
    }

    private void uploadData(@NotNull Person save) {
        CollectionReference db = Variables.Firebase.FIRESTORE.collection("Person");

        String id = save.getFirst_Name().substring(0, 2) + save.getLast_Name().substring(0, 2)
                + "-" + save.getUid();
        FirebaseUser user = Variables.Firebase.AUTHENTICATION.getCurrentUser();
        if (user != null) {
            user.updateEmail(save.getMail());
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email send");

                            Toast.makeText(getContext(), getString(R.string.email_verification_send), Toast.LENGTH_SHORT).show();
                        }
                    });

            db.document(id).set(save)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Create connection from realtime to fireStore
                            //createConnection(save.getId(), Variables.Firebase.AUTHENTICATION.getUid());
                            Log.d(TAG, "Userdata upload successful");
                        } else {
                            Log.d(TAG, "something went wrong with the upload of the user data", task.getException());
                            error("upload_error");
                        }
                    });
            dismiss();
        }
    }

}