package de.rwu.wi.gui;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import de.rwu.wi.parallel.SimpleParallelMergeSortedArrayList;
import de.rwu.wi.parallel.SimpleParallelQuickSortedArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceRunnerTest {
    @Test
    void generatesIntegersAndFixedLengthStrings() {
        List<? extends Comparable<?>> integers =
                PerformanceRunner.createData(25, PerformanceRunner.DataType.INTEGER, new Random(1));
        List<? extends Comparable<?>> strings =
                PerformanceRunner.createData(25, PerformanceRunner.DataType.STRING, new Random(1));
        assertEquals(25, integers.size());
        assertEquals(25, strings.size());
        assertTrue(integers.stream().allMatch(Integer.class::isInstance));
        assertTrue(strings.stream().allMatch(value -> value instanceof String && ((String) value).length() == 10));
        assertThrows(IllegalArgumentException.class,
                () -> PerformanceRunner.createData(-1, PerformanceRunner.DataType.INTEGER, new Random()));
    }

    @Test
    void runsEveryAlgorithmOnBothTypesWithoutModifyingInput() {
        for (PerformanceRunner.DataType type : PerformanceRunner.DataType.values()) {
            List<? extends Comparable<?>> input = PerformanceRunner.createData(41, type, new Random(42));
            List<? extends Comparable<?>> original = new ArrayList<>(input);
            for (PerformanceRunner.Algorithm algorithm : PerformanceRunner.Algorithm.values()) {
                PerformanceRunner.Result result = PerformanceRunner.run(algorithm, input, type);
                assertTrue(result.elapsedNanos() >= 0, algorithm.toString());
                assertTrue(result.comparisons() > 0, algorithm.toString());
                assertEquals(original, input, algorithm.toString());
            }
        }
    }

    @Test
    void handlesEmptyAndSingletonInputs() {
        for (PerformanceRunner.Algorithm algorithm : PerformanceRunner.Algorithm.values()) {
            assertEquals(0, PerformanceRunner.run(algorithm, List.<Integer>of(),
                    PerformanceRunner.DataType.INTEGER).comparisons(), algorithm.toString());
            assertEquals(0, PerformanceRunner.run(algorithm, List.of(42),
                    PerformanceRunner.DataType.INTEGER).comparisons(), algorithm.toString());
        }
    }

    @Test
    void parallelComparisonCountsMatchAnIndependentAtomicCounter() {
        List<Integer> input = new ArrayList<>();
        Random random = new Random(42);
        for (int i = 0; i < 10_000; i++) {
            input.add(random.nextInt(10_000));
        }
        AtomicLong mergeCount = new AtomicLong();
        try (SimpleParallelMergeSortedArrayList<Integer> merge =
                     new SimpleParallelMergeSortedArrayList<>(input.size())) {
            merge.add(input);
            merge.sort((a, b) -> {
                mergeCount.incrementAndGet();
                return Integer.compare(a, b);
            });
        }
        assertEquals(mergeCount.get(), PerformanceRunner.run(PerformanceRunner.Algorithm.PARALLEL_MERGE,
                input, PerformanceRunner.DataType.INTEGER).comparisons());

        AtomicLong quickCount = new AtomicLong();
        try (SimpleParallelQuickSortedArrayList<Integer> quick =
                     new SimpleParallelQuickSortedArrayList<>(input.size())) {
            quick.add(input);
            quick.sort((a, b) -> {
                quickCount.incrementAndGet();
                return Integer.compare(a, b);
            });
        }
        assertEquals(quickCount.get(), PerformanceRunner.run(PerformanceRunner.Algorithm.PARALLEL_QUICK,
                input, PerformanceRunner.DataType.INTEGER).comparisons());
    }

    @Test
    void abortsEveryAlgorithmDuringSortingAndAllowsTheNextRun() throws Exception {
        List<Integer> input = new ArrayList<>();
        for (int i = 4095; i >= 0; i--) {
            input.add(i);
        }
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            for (PerformanceRunner.Algorithm algorithm : PerformanceRunner.Algorithm.values()) {
                SortCancellation cancellation = new SortCancellation();
                CountDownLatch comparisonStarted = new CountDownLatch(1);
                CountDownLatch resume = new CountDownLatch(1);
                AtomicBoolean first = new AtomicBoolean(true);
                Future<PerformanceRunner.Result> future = executor.submit(() ->
                        PerformanceRunner.runTyped(algorithm, input, (a, b) -> {
                            if (first.compareAndSet(true, false)) {
                                comparisonStarted.countDown();
                                try {
                                    if (!resume.await(5, TimeUnit.SECONDS)) {
                                        throw new IllegalStateException("Test timed out");
                                    }
                                } catch (InterruptedException exception) {
                                    Thread.currentThread().interrupt();
                                    throw new IllegalStateException(exception);
                                }
                            }
                            return Integer.compare(a, b);
                        }, cancellation));
                try {
                    assertTrue(comparisonStarted.await(5, TimeUnit.SECONDS), algorithm.toString());
                    cancellation.cancel();
                } finally {
                    resume.countDown();
                }
                ExecutionException exception = assertThrows(ExecutionException.class,
                        () -> future.get(5, TimeUnit.SECONDS), algorithm.toString());
                assertTrue(exception.getCause() instanceof CancellationException
                        || exception.getCause().getCause() instanceof CancellationException,
                        algorithm + ": " + exception.getCause());
            }
        }
        assertTrue(PerformanceRunner.run(PerformanceRunner.Algorithm.ARRAY_MERGE,
                List.of(3, 1, 2), PerformanceRunner.DataType.INTEGER).comparisons() > 0);
    }

    @Test
    void cancellationDoesNotCarryOverToTheNextSorter() {
        ActiveSort active = new ActiveSort();
        assertTrue(!active.abort());
        SortCancellation first = active.begin();
        assertTrue(active.abort());
        assertTrue(active.finish(first));
        SortCancellation second = active.begin();
        assertTrue(!second.isCancelled());
        assertTrue(!active.finish(second));
        assertTrue(!active.abort());
    }
}
