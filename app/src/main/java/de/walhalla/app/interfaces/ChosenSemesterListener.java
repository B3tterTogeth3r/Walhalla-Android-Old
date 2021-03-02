package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.App;
import de.walhalla.app.models.Semester;

public interface ChosenSemesterListener {

    default void changeSemesterDone() {
    }

    default void start(@NotNull Semester chosenSemester) {
        App.setChosenSemester(chosenSemester);
        changeSemesterDone();
    }

    default void joinedSemesterDone(@NotNull Semester chosenSemester) {
    }

}
