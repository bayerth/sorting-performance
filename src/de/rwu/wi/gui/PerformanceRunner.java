package de.rwu.wi.gui;

import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.parallel.SimpleParallelMergeSortedArrayList;
import de.rwu.wi.parallel.SimpleParallelQuickSortedArrayList;
import de.rwu.wi.sequential.array.SimpleArrayList;
import de.rwu.wi.sequential.array.SimpleMergeSortedArrayList;
import de.rwu.wi.sequential.array.SimpleQuickSortedArrayList;
import de.rwu.wi.sequential.linkedlist.LinkedListMergeSorter;
import de.rwu.wi.sequential.linkedlist.SimpleLinkedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

final class PerformanceRunner {
    enum Algorithm {
        ARRAY_BUBBLE("Array bubble sort"),
        ARRAY_MERGE("Array merge sort"),
        ARRAY_QUICK("Array quicksort"),
        LINKED_BUBBLE("Linked-list bubble sort"),
        LINKED_MERGE("Linked-list merge sort"),
        PARALLEL_MERGE("Parallel merge sort"),
        PARALLEL_QUICK("Parallel quicksort"),
        JAVA_SORT("Java ArrayList.sort");

        private final String label;

        Algorithm(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    enum DataType {
        INTEGER("Integer"),
        STRING("String (10 characters)");

        private final String label;

        DataType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    record Result(long elapsedNanos, long comparisons) {}

    private PerformanceRunner() {}

    static List<? extends Comparable<?>> createData(int size, DataType type, Random random) {
        if (size < 0) {
            throw new IllegalArgumentException("List size cannot be negative");
        }
        if (type == DataType.INTEGER) {
            List<Integer> data = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                data.add(random.nextInt());
            }
            return data;
        }
        List<String> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            StringBuilder value = new StringBuilder(10);
            for (int j = 0; j < 10; j++) {
                value.append((char) ('a' + random.nextInt(26)));
            }
            data.add(value.toString());
        }
        return data;
    }

    static Result run(Algorithm algorithm, List<? extends Comparable<?>> data, DataType type) {
        return run(algorithm, data, type, new SortCancellation());
    }

    static Result run(Algorithm algorithm, List<? extends Comparable<?>> data, DataType type,
                      SortCancellation cancellation) {
        if (type == DataType.INTEGER) {
            return runTyped(algorithm, checkedData(data, Integer.class, cancellation),
                    Comparator.naturalOrder(), cancellation);
        }
        return runTyped(algorithm, checkedData(data, String.class, cancellation),
                Comparator.naturalOrder(), cancellation);
    }

    private static <T> List<T> checkedData(List<?> data, Class<T> elementType, SortCancellation cancellation) {
        cancellation.check();
        List<T> result = new ArrayList<>(data.size());
        for (Object value : data) {
            cancellation.check();
            result.add(elementType.cast(value));
        }
        return result;
    }

    static <T> Result runTyped(Algorithm algorithm, List<T> data, Comparator<T> order,
                               SortCancellation cancellation) {
        cancellation.check();
        LongAdder comparisons = new LongAdder();
        Comparator<T> countingComparator = (left, right) -> {
            cancellation.check();
            comparisons.increment();
            return order.compare(left, right);
        };
        if (algorithm == Algorithm.JAVA_SORT) {
            List<T> copy = new ArrayList<>(data);
            long start = System.nanoTime();
            copy.sort(countingComparator);
            long elapsed = System.nanoTime() - start;
            verifySorted(copy, order, cancellation);
            cancellation.check();
            return new Result(elapsed, comparisons.sum());
        }

        ISimpleList<T> list = createList(algorithm, data.size());
        try {
            for (T value : data) {
                cancellation.check();
                if (!list.add(value)) {
                    throw new IllegalStateException("Sorter could not accept all input values");
                }
            }
            long start = System.nanoTime();
            list.sort(countingComparator);
            long elapsed = System.nanoTime() - start;
            if (list.size() != data.size()) {
                throw new IllegalStateException("Sorter changed the number of elements");
            }
            for (int i = 1; i < list.size(); i++) {
                cancellation.check();
                if (order.compare(list.get(i - 1), list.get(i)) > 0) {
                    throw new IllegalStateException("Sorter returned unordered elements");
                }
            }
            cancellation.check();
            return new Result(elapsed, comparisons.sum());
        } finally {
            if (list instanceof SimpleParallelMergeSortedArrayList<?> merge) {
                merge.close();
            } else if (list instanceof SimpleParallelQuickSortedArrayList<?> quick) {
                quick.close();
            }
        }
    }

    private static <T> void verifySorted(List<T> list, Comparator<T> comparator,
                                         SortCancellation cancellation) {
        for (int i = 1; i < list.size(); i++) {
            cancellation.check();
            if (comparator.compare(list.get(i - 1), list.get(i)) > 0) {
                throw new IllegalStateException("Sorter returned unordered elements");
            }
        }
    }

    private static <T> ISimpleList<T> createList(Algorithm algorithm, int size) {
        return switch (algorithm) {
            case ARRAY_BUBBLE -> new SimpleArrayList<>(size);
            case ARRAY_MERGE -> new SimpleMergeSortedArrayList<>(size);
            case ARRAY_QUICK -> new SimpleQuickSortedArrayList<>(size);
            case LINKED_BUBBLE -> new SimpleLinkedList<>(size);
            case LINKED_MERGE -> new LinkedListMergeSorter<>(size);
            case PARALLEL_MERGE -> new SimpleParallelMergeSortedArrayList<>(size);
            case PARALLEL_QUICK -> new SimpleParallelQuickSortedArrayList<>(size);
            case JAVA_SORT -> throw new IllegalArgumentException("Java sort does not use ISimpleList");
        };
    }
}
