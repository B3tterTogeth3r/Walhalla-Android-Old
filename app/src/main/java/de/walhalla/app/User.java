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
    static UserDataChangeListener listener;
    private static Person userData = new Person();
    private static Map<String, Object> admins;

    public User(UserDataChangeListener listener) {
        User.listener = listener;
    }

    public static boolean isLogIn() {
        Log.i(TAG, "is user login: " + (userData != null));
        return userData != null;
    }

    public static void setData(@NotNull Person userData, @NotNull String username) {
        User.userData = userData;
        User.username = username;
        Log.i(TAG, "Userdata got changed for user-id " + userData.getId());
    }

    public static Person getData() {
        if (userData != null) {
            return userData;
        } else {
            return new Person();
        }
    }

    public static void logOut() {
        userData = null;
        username = "";
        //Clear FirebaseAuth
        Variables.Firebase.isUserLogin = false;
        try {
            Variables.Firebase.AUTHENTICATION.signOut();
        } catch (Exception ignored){
        }
        if (listener != null) {
            listener.userDataChangeListener();
        }
    }

    public static boolean hasCharge() {
        Log.i(TAG, "User has charge: " + (App.getCurrentChargen().contains(username) || isAdmin()));
        return (App.getCurrentChargen().contains(username) || isAdmin());
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
        Map<String, Object> list = getAdmins();
        try {
            if (list.get(username).equals("super-admin"))
                return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static Map<String, Object> getAdmins() {
        return admins;
    }

    public static void setAdmins(Map<String, Object> admins) {
        if (admins != null) {
            System.out.println(admins.size());
        }
        User.admins = admins;
    }
}