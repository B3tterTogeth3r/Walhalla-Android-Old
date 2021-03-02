package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.models.Cashbox;

public interface CustomClickListener {
    default void customOnClick(@NotNull Cashbox selected) {
    }
}