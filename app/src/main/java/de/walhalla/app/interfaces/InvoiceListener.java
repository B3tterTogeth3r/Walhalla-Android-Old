package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.models.Drink;

public interface InvoiceListener {
    default void amountChangeListener(int amount, @NotNull Drink kind) {
    }
}
