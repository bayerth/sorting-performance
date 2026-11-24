package de.rwu.wi.parallel;

import de.rwu.wi.sequential.array.SimpleMergeSortedArrayList;

import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SimpleParallelMergeSortedArrayList<E> extends SimpleMergeSortedArrayList<E> implements AutoCloseable {
    private static final int THRESHOLD = 1000;
    private final ForkJoinPool pool;

    public SimpleParallelMergeSortedArrayList(int n) {
        super(n);
        pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public void sort(Comparator<E> comparator) {
        if (size() > 1) {
            pool.invoke(new MergeSortTask(0, size() - 1, comparator));
        }

        isSorted = true;
    }

    @Override
    public void close() {
        pool.close();
    }

    private class MergeSortTask extends RecursiveAction {
        private final int low;
        private final int high;
        private final Comparator<E> comparator;

        MergeSortTask(int low, int high, Comparator<E> comparator) {
            this.low = low;
            this.high = high;
            this.comparator = comparator;
        }

        @Override
        protected void compute() {
            if (high - low < THRESHOLD) {
                mergeSort(array, low, high, comparator);
                return;
            }

            int mid = low + (high - low) / 2;
            invokeAll(
                    new MergeSortTask(low, mid, comparator),
                    new MergeSortTask(mid + 1, high, comparator)
            );
            merge(array, low, mid, high, comparator);
        }
    }
}
