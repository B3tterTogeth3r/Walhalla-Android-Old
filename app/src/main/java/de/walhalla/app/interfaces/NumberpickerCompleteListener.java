package de.walhalla.app.interfaces;

import de.walhalla.app.models.AllEvents;
import de.walhalla.app.models.Event;

public interface NumberpickerCompleteListener {
    default void notifyOfNumberPickerDone(AllEvents allEvents) {
    }

    default void notifyOfNumberPickerDone(Event event) {
    }
}
