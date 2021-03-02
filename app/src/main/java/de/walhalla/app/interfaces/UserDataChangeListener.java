package de.walhalla.app.interfaces;

public interface UserDataChangeListener {
    default void ListenerForAll() {
        userDataChangeListener();
        ChangeListenerLeftNav();
    }

    default void userDataChangeListener() {
    }

    default void ChangeListenerLeftNav() {
    }
}
