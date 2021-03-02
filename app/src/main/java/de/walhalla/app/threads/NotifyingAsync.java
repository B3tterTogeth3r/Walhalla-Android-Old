package de.walhalla.app.threads;

import android.os.AsyncTask;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import de.walhalla.app.App;
import de.walhalla.app.interfaces.RunnableCompleteListener;

public abstract class NotifyingAsync extends AsyncTask<Void, Void, Void> {
    public boolean internet = App.getInternet();
    private final Set<RunnableCompleteListener> listeners = new CopyOnWriteArraySet<>();

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
    protected Void doInBackground(Void... voids) {
        try {
            backgroundWorker();
        } finally {
            notifyListeners();
        }
        return null;
    }

    public abstract void backgroundWorker();
}
