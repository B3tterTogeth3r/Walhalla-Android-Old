package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

public interface CouleurTimePickerListener {
    void punctualityDone(@NotNull String punctuality);
    void collarDone(String collar);
}
