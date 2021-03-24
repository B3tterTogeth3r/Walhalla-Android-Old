package de.walhalla.app.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface personSelectorListener {
    void onPersonSelectorDone(int which, @NotNull List<String> names);
}