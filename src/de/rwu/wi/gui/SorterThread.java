package de.rwu.wi.gui;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

final class SorterThread {
    private SorterThread() {}

    static <T> T run(Callable<T> sorter, SortCancellation cancellation) {
        FutureTask<T> task = new FutureTask<>(sorter);
        Thread thread = new Thread(task, "performance-sorter");
        thread.start();
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException exception) {
                    interrupted = true;
                    cancellation.cancel();
                }
            }
            if (interrupted) {
                throw new IllegalStateException("Sorter coordinator was interrupted");
            }
            try {
                return task.get();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Sorter coordinator was interrupted", exception);
            } catch (ExecutionException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof RuntimeException runtime) {
                    throw runtime;
                }
                if (cause instanceof Error error) {
                    throw error;
                }
                throw new IllegalStateException("Sorter failed", cause);
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
