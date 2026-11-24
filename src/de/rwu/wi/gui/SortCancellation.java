package de.rwu.wi.gui;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

final class SortCancellation {
    private final AtomicBoolean cancelled = new AtomicBoolean();

    void cancel() {
        cancelled.set(true);
    }

    boolean isCancelled() {
        return cancelled.get();
    }

    void check() {
        if (isCancelled()) {
            throw new CancellationException("Aborted");
        }
    }
}
