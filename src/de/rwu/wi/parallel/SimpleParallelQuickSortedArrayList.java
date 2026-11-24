package de.rwu.wi.parallel;

import de.rwu.wi.sequential.array.SimpleQuickSortedArrayList;

import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SimpleParallelQuickSortedArrayList<E> extends SimpleQuickSortedArrayList<E> implements AutoCloseable {

    private static final int THRESHOLD = 1000;
    private final ForkJoinPool pool;

    public SimpleParallelQuickSortedArrayList(int n) {
        super(n);
        pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public SimpleParallelQuickSortedArrayList(int n, int nrOfThreads) {
        super(n);
        pool = new ForkJoinPool(nrOfThreads > 0 ? nrOfThreads : 1);
    }

    @Override
    public void sort(Comparator<E> comparator) {
        if (size() > 1) {
            pool.invoke(new QuickSortTask(0, size() - 1, comparator));
        }

        isSorted = true;
    }

    @Override
    public void close() {
        pool.close();
    }

    private class QuickSortTask extends RecursiveAction {
        private final int low;
        private final int high;
        private final Comparator<E> comparator;

        QuickSortTask(int low, int high, Comparator<E> comparator) {
            this.low = low;
            this.high = high;
            this.comparator = comparator;
        }

        @Override
        protected void compute() {
            if (high - low < THRESHOLD) {
                quicksort(array, low, high, comparator);
                return;
            }

            int mid = low + (high - low) / 2;
            QuickSortTask left = new QuickSortTask(low, mid, comparator);
            QuickSortTask right = new QuickSortTask(mid + 1, high, comparator);
            invokeAll(left, right);
            merge(array, low, mid, high, comparator);
        }
    }

    protected void merge(E[] array, int low, int mid, int high, Comparator<E> comparator) {

        E[] aux = (E[]) new Object[high - low + 1];
        if(high + 1 - low >= 0) { System.arraycopy(array, low, aux, low - low, high + 1 - low); }
        int i = low;
        int j = mid + 1;
        for(int k = low; k <= high; k++) {

            if(i > mid) { // loop
                array[k] = aux[j - low];
                j++;
            }
            else if(j > high) { // loop
                array[k] = aux[i - low];
                i++;
            }
            else {
                if(comparator.compare(aux[i - low], aux[j - low]) <= 0) {
                    array[k] = aux[i - low];
                    i++;
                }
                else {
                    array[k] = aux[j - low];
                    j++;
                }
            }
        }
    }

}
