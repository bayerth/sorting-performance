package de.rwu.wi.gui;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SorterThreadTest {
    @Test
    void runsOnASeparateNonUiThreadAndWaitsForTermination() {
        Thread coordinator = Thread.currentThread();
        AtomicReference<Thread> sorter = new AtomicReference<>();
        int result = SorterThread.run(() -> {
            sorter.set(Thread.currentThread());
            assertFalse(SwingUtilities.isEventDispatchThread());
            return 42;
        }, new SortCancellation());
        assertEquals(42, result);
        assertNotSame(coordinator, sorter.get());
        assertFalse(sorter.get().isAlive());
    }

    @Test
    void cancellationTerminatesTheSorterBeforeReturning() throws Exception {
        SortCancellation cancellation = new SortCancellation();
        CountDownLatch started = new CountDownLatch(1);
        AtomicReference<Thread> sorter = new AtomicReference<>();
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> result = executor.submit(() -> SorterThread.run(() -> {
                sorter.set(Thread.currentThread());
                started.countDown();
                while (true) {
                    cancellation.check();
                    Thread.onSpinWait();
                }
            }, cancellation));
            assertTrue(started.await(5, TimeUnit.SECONDS));
            cancellation.cancel();
            ExecutionException exception = assertThrows(ExecutionException.class,
                    () -> result.get(5, TimeUnit.SECONDS));
            assertTrue(exception.getCause() instanceof CancellationException);
            assertFalse(sorter.get().isAlive());
        }
    }
}
