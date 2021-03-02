package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.models.DrinkPrice;

public interface InvoiceListener {
    default void amountChangeListener(int amount, @NotNull DrinkPrice kind) {
    }
}
