package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.models.Accounting;

public interface CustomClickListener {
    default void customOnClick(@NotNull Accounting selected) {
    }
}