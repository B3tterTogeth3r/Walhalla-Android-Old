package de.walhalla.app;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import de.walhalla.app.interfaces.UserDataChangeListener;
import de.walhalla.app.models.Person;
import de.walhalla.app.utils.Variables;

public class User {
    private static final String TAG = "User";
    public static String username;
    private static Person userData = new Person();
    static UserDataChangeListener listener;

    public User(UserDataChangeListener listener) {
        User.listener = listener;
    }

    public static boolean isLogIn() {
        Log.i(TAG, "is user login: " + (Variables.Firebase.AUTHENTICATION != null));
        return Variables.Firebase.AUTHENTICATION != null;
    }

    public static void setData(@NotNull Person userData, @NotNull String username) {
        User.userData = userData;
        User.username = username;
        Log.i(TAG, "Userdata got changed for user-id " + userData.getId());
    }

    public static Person getData() {
        return userData;
    }

    public static void logOut() {
        userData = new Person();
        username = "";
        //Clear FirebaseAuth
        Variables.Firebase.AUTHENTICATION.signOut();
        if (listener != null) {
            listener.userDataChangeListener();
        }
    }

    public static boolean hasCharge() {
        Log.i(TAG, "User has charge: " + (App.getCurrentChargen().contains(username) | isAdmin()));
        return App.getCurrentChargen().contains(username) | isAdmin();
    }

    public static boolean isX() {
        return App.getCurrentChargen().get(0).equals(username) && hasCharge();
    }

    public static boolean isVX() {
        if (isX())
            return true;
        return App.getCurrentChargen().get(1).equals(username) && hasCharge();
    }

    public static boolean isFM() {
        if (isX())
            return true;
        if (isVX())
            return true;
        return App.getCurrentChargen().get(2).equals(username) && hasCharge();
    }

    public static boolean isXX() {
        if (isX())
            return true;
        if (isVX())
            return true;
        if (isFM())
            return true;
        return App.getCurrentChargen().get(3).equals(username) && hasCharge();
    }

    public static boolean isXXX() {
        if (isX())
            return true;
        if (isVX())
            return true;
        if (isFM())
            return true;

        return App.getCurrentChargen().get(4).equals(username) && hasCharge();
    }

    private static boolean isAdmin() {
        final boolean[] returnValue = {false};
        Variables.Firebase.FIRESTORE.collection("Editors").document("private").get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> admins = documentSnapshot.getData();
            if (admins != null && !admins.isEmpty()) {
                for (int i = 0; i < admins.size(); i++) {
                    try {
                        if (((String) admins.get(username)).equals("super-admin"))
                            returnValue[0] = true;
                    } catch (Exception exception) {
                        returnValue[0] = false;
                    }
                }
            }
        });
        return returnValue[0] && (Variables.Firebase.AUTHENTICATION != null);
    }
}