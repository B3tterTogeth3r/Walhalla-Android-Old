package de.walhalla.app.threads;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import de.walhalla.app.App;
import de.walhalla.app.interfaces.RunnableCompleteListener;

public abstract class NotifyingRunnable implements Runnable {
    public final boolean internet = App.getInternet();
    public static Set<RunnableCompleteListener> listeners = new CopyOnWriteArraySet<>();

    public final void addListener(final RunnableCompleteListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(final RunnableCompleteListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (RunnableCompleteListener listener : listeners) {
            listener.notifyOfRunnableComplete(this);
        }
    }

    @Override
    public void run() {
        try {
            doRun();
        } finally {
            notifyListeners();
        }
    }

    public abstract void doRun();
}
