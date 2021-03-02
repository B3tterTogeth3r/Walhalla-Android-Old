package de.walhalla.app.interfaces;

import java.util.ArrayList;

import de.walhalla.app.models.Event;
import de.walhalla.app.models.Helper;
import de.walhalla.app.models.HelperKind;


public interface personSelectorListener {
    default void onPersonSelectorDone(Event event, ArrayList<Helper> helperArrayList, HelperKind kind) {
    }
}