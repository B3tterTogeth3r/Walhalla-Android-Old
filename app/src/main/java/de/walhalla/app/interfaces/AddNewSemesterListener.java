package de.walhalla.app.interfaces;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.walhalla.app.models.Event;
import de.walhalla.app.models.Semester;

public interface AddNewSemesterListener {
    void semesterDone(@NotNull Semester semester);
    void chargenDone(@NotNull ArrayList<Object> chargenList);
    void philChargenDone(@NotNull ArrayList<Object> philChargenList);
    void eventsDone(@NotNull ArrayList<Event> eventsList);
    void greetingDone(@NotNull ArrayList<Object> greetingList);
    void notesDone(@NotNull ArrayList<Object> notesList);
    void messageDone(@Nullable ArrayList<Object> message);
}
