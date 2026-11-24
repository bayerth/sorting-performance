package de.rwu.wi.sequential.array;

import java.util.Comparator;


public class SimpleQuickSortedArrayList<E> extends SimpleArrayList<E> {

    public static final int PIVOT_SELECTION_BOUND = 50;

    public SimpleQuickSortedArrayList(int n) {
        super(n);
    }

    @Override
    public void sort(Comparator<E> comparator) {
        // print size of array
        quicksort(array, 0, size() - 1, comparator);
        isSorted = true;
    }

    protected void quicksort(E[] array, int low, int high, Comparator<E> comparator) {
        if(low < high) {
            int pivotIndex = partition(array, low, high, comparator);
            quicksort(array, low, pivotIndex - 1, comparator);
            quicksort(array, pivotIndex + 1, high, comparator);

        }
    }

    private int partition(E[] array, int low, int high, Comparator<E> comparator) {
        int i = low;
        int j = high + 1;
        E pivot = getPivotIndex(low, high, comparator);

        while(true) {
            while(comparator.compare(array[++i], pivot) <= 0) {
                if(i == high) {
                    break;
                }
            }
            while(comparator.compare(pivot, array[--j]) < 0) {
                if(j == low) {
                    break;
                }
            }
            if(i >= j) {
                break;
            }
            swap(array, i, j);
        }
        swap(array, low, j);
        return j;
    }

    private E getPivotIndex(int low, int high, Comparator<E> comparator) {
        // median of first, middle and last element is used as pivot
        // index of pivot element = low
        //if(low + PIVOT_SELECTION_BOUND > high) { return low; }
        int mid = low + (high - low) / 2;

        if(comparator.compare(array[mid], array[low]) > 0) {
            swap(array, low, mid); //      swap A[lo] with A[mid]
        }
        if(comparator.compare(array[high], array[low]) > 0) {
            swap(array, low, high); //        swap A[lo] with A[hi]
            if(comparator.compare(array[mid], array[high]) > 0) {
                swap(array, low, high); //      swap A[mid] with A[hi]
            }
        }
        return array[low];

    }
    
    private int checkPartition(E[] array, int low, int pivotIndex, int high,
                               Comparator<E> comparator) {
        for(int i = low; i < pivotIndex; i++) {
            if(comparator.compare(array[i], array[pivotIndex]) > 0) {
                return i;
            }
        }
        for(int i = pivotIndex + 1; i <= high; i++) {
            if(comparator.compare(array[i], array[pivotIndex]) < 0) {
                return i;
            }
        }
        return pivotIndex;
    }


    private void swap(E[] array, int i, int j) {
        E temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
