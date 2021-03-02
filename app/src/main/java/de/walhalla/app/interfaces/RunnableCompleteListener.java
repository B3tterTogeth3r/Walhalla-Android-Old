package de.walhalla.app.interfaces;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import de.walhalla.app.threads.NotifyingAsync;
import de.walhalla.app.threads.NotifyingRunnable;

public interface RunnableCompleteListener {

    Set<NotifyingRunnable> runnables = new CopyOnWriteArraySet<>();
    Set<NotifyingAsync> asyncs = new CopyOnWriteArraySet<>();

    default void notifyOfRunnableComplete(final NotifyingRunnable runnable) {
        //runnables.add(runnable);

    }

    default void notifyOfRunnableComplete(final NotifyingAsync runnable) {
        //asyncs.add(runnable);
    }
}
