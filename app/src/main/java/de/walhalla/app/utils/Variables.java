package de.walhalla.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.walhalla.app.App;
import de.walhalla.app.R;
import de.walhalla.app.User;
import de.walhalla.app.firebase.CustomAuthListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.models.Semester;

@SuppressLint("StaticFieldLeak")
public class Variables {
    public static final String FIREBASE_INSTANT_ID = "0a6a59be-b398-4832-8b71-1fc4ccaf8275";
    public static final Locale LOCALE = new Locale("de", "DE");
    public static final String EDIT = "edit";
    public static final String ADD = "add";
    public static final String DETAILS = "details";
    public static final String DELETE = "delete";
    public static final String SHOW = "show";
    public static final String[] MONTHS = {App.getContext().getString(R.string.month_jan), App.getContext().getString(R.string.month_feb), App.getContext().getString(R.string.month_mar), App.getContext().getString(R.string.month_apr), App.getContext().getString(R.string.month_may),
            App.getContext().getString(R.string.month_jun), App.getContext().getString(R.string.month_jul), App.getContext().getString(R.string.month_aug), App.getContext().getString(R.string.month_sep), App.getContext().getString(R.string.month_oct), App.getContext().getString(R.string.month_nov), App.getContext().getString(R.string.month_dec)};
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final long ONE_MEGABYTE = 1024 * 1024 * 2;
    public static final float SCALE = App.getContext().getResources().getDisplayMetrics().density;
    public static ArrayList<Semester> SEMESTER_ARRAY_LIST;
    private static final boolean[] setters = new boolean[4];

    public static boolean setFirebase() {
        setters[0] = setAllSemesters();
        setters[1] = Firebase.setFirestoreDB();
        setters[2] = Firebase.setAuth();
        setters[3] = Firebase.setStorage();
        return (setters[0] && setters[1] && setters[2] && setters[3]);
    }

    private static boolean setAllSemesters() {
        try {
            SEMESTER_ARRAY_LIST = new ArrayList<>();
            Semester current = new Semester();
            current.setID(1);
            Calendar c = Calendar.getInstance(LOCALE);
            c.set(1864, 10, 7);
            current.setBegin(c.getTime());
            c.set(3000, 11, 31);
            current.setEnd(c.getTime());
            current.setLong("Admin mode");
            current.setShort("Admin");
            SEMESTER_ARRAY_LIST.add(current);

            current.setID(2);
            c.set(1864, 10, 7);
            current.setBegin(c.getTime());
            c.set(3000, 11, 31);
            current.setEnd(c.getTime());
            current.setLong("Admin mode");
            current.setShort("Admin");
            SEMESTER_ARRAY_LIST.add(current);
            int year = 1864;
            for (int i = 3; i < 358; i++) {
                //Add winter semester
                Semester ws = new Semester();
                ws.setID(i);
                c.set(year, 9, 0);
                ws.setBegin(c.getTime());
                String back = String.valueOf(year + 1).substring(2);
                ws.setLong(App.getContext().getString(R.string.ws) + " " + year + "/" + back);
                ws.setShort("WS" + year + "/" + back);
                year++;
                c.set(year, 2, 31, 23, 59, 59);
                ws.setEnd(c.getTime());
                setCurrent(ws);
                SEMESTER_ARRAY_LIST.add(ws);
                //Add summer semester
                i++;
                Semester ss = new Semester();
                ss.setID(i);
                c.set(year, 3, 0);
                ss.setBegin(c.getTime());
                ss.setLong(App.getContext().getString(R.string.ss) + " " + year);
                ss.setShort("SS" + year);
                c.set(year, 8, 30, 23, 59, 59);
                ss.setEnd(c.getTime());
                setCurrent(ss);
                SEMESTER_ARRAY_LIST.add(ss);
            }
            Log.i("CreateSem", "size: " + SEMESTER_ARRAY_LIST.size());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void setCurrent(@NotNull Semester current) {
        Calendar datum = Calendar.getInstance(LOCALE);
        if (current.getBegin().before(datum.getTime()) && current.getEnd().after(datum.getTime())) {
            App.setCurrentSemester(current);
        } else if (current.getBegin().equals(datum.getTime())) {
            App.setCurrentSemester(current);
        } else if (current.getEnd().equals(datum.getTime())) {
            App.setCurrentSemester(current);
        }
    }

    public static class Firebase {
        public static FirebaseDatabase DATABASE;
        public static StorageReference IMAGES;
        public static StorageReference RECEIPTS;
        public static FirebaseAuth AUTHENTICATION;
        public static boolean isUserLogin = false;
        public static FirebaseUser user;
        public static FirebaseFirestore FIRESTORE;

        public static boolean setFirestoreDB() {
            try {
                FIRESTORE = FirebaseFirestore.getInstance();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static boolean setAuth() {
            try {
                AUTHENTICATION = FirebaseAuth.getInstance();
                //com.example.walhalla.firebase.Firebase
                AUTHENTICATION.addAuthStateListener(new CustomAuthListener());
                Firebase.user = AUTHENTICATION.getCurrentUser();
                if (user != null) {
                    Firebase.FIRESTORE
                            .collection("Person")
                            .whereEqualTo("uid", user.getUid())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        Person p = snapshot.toObject(Person.class);
                                        try {
                                            if (p != null) {
                                                User.setData(p, Objects.requireNonNull(user.getEmail()));
                                            } else {
                                                throw new Exception("No person for that user");
                                            }
                                        } catch (Exception e) {
                                            User.logOut();
                                            AUTHENTICATION.signOut();
                                            user = null;
                                        }
                                    }
                                }
                            });
                } else {
                    User.logOut();
                    AUTHENTICATION.signOut();
                    user = null;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static boolean setStorage() {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://walhallaapp.appspot.com");
            try {
                IMAGES = storage.getReference().child("pictures");
                RECEIPTS = storage.getReference().child("receipts");
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static class Reference {
            public static DatabaseReference PICTURE;
            public static DatabaseReference NEWS;
            public static DatabaseReference ALL_EVENTS;
            public static DatabaseReference CASHBOX;
            public static DatabaseReference CHARGEN;
            public static DatabaseReference DATA;
            public static DatabaseReference DRINK;
            public static DatabaseReference DRINK_KIND;
            public static DatabaseReference DRINK_PRICE;
            public static DatabaseReference DRIVE;
            public static DatabaseReference EVENT;
            public static DatabaseReference HELPER;
            public static DatabaseReference HELPER_KIND;
            public static DatabaseReference MEETING;
            public static DatabaseReference PERSON;
            public static DatabaseReference SEMESTER;
            public static DatabaseReference RANK;
        }

        public static class Auth {
            private final static String TAG = "FirebaseAuth";

            public static boolean createNewUserEmail(String email, String password) {
                AtomicBoolean result = new AtomicBoolean(false);
                AUTHENTICATION.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) App.getContext(), task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = AUTHENTICATION.getCurrentUser();
                                result.set(true);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(App.getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                result.set(false);
                            }
                        });
                return result.get();
            }
        }
    }

    public static class Walhalla {
        public static final String NAME = "K.St.V. Walhalla im KV zu Würzburg";
        public static final String ADH_ADDRESS = "Mergentheimer Straße 32\n97082 Würzburg";
        public static final String ADH_NAME = "adH";
        public static final GeoPoint ADH_LOCATION = new GeoPoint(49.784389, 9.924648);
        public static final String MAIL_SENIOR = "senior@walhalla-wuerzburg.de";
        public static final String MAIL_CONSENIOR = "consenior@walhalla-wuerzburg.de";
        public static final String MAIL_FUXMAJOR = "fuchsmajor@walhalla-wuerzburg.de";
        public static final String MAIL_SCHRIFTFUEHRER = "scriptor@walhalla-wuerzburg.de";
        public static final String MAIL_KASSIER = "kassier@walhalla-wuerzburg.de";
        public static final String MAIL_SENIOR_PHIL = "ahx@walhalla-wuerzburg.de";
        public static final String MAIL_CONSENIOR_PHIL = "ahxx@walhalla-wuerzburg.de";
        public static final String MAIL_FUXMAJOR_PHIL = "ahxxx@walhalla-wuerzburg.de";
        public static final String MAIL_WH_PHIL = "hausverwaltung@walhalla-wuerzburg.de";
        public static final String SENIOR = "Senior";
        public static final String CONSENIOR = "Consenior";
        public static final String FUXMAJOR = "Fuxmajor";
        public static final String SCHRIFTFUEHRER = "Schriftführer";
        public static final String KASSIER = "Kassier";
        public static final String WEBSITE = "http://walhalla-wuerzburg.de";
    }
}
