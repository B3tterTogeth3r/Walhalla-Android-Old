package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface NumberPickerCompleteListener {
    void notifyOfAccountingDone(String name);

    void notifyOfTaskDone(@NotNull Map<String, Object> helpers);
}
