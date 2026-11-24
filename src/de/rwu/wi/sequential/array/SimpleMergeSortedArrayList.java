package de.rwu.wi.sequential.array;

import de.rwu.wi.sequential.array.SimpleArrayList;

import java.util.Comparator;


public class SimpleMergeSortedArrayList<E> extends SimpleArrayList<E> {

    public SimpleMergeSortedArrayList(int n) {
        super(n);
    }

    @Override
    public void sort(Comparator<E> comparator) {
        mergeSort(array, 0, this.size() - 1, comparator);
        isSorted = true;
    }

    protected void mergeSort(E[] array, int low, int high, Comparator<E> comparator) {
        if (low < high) {

            int mid = low + (high - low) / 2;

            // System.out.println("sort " + low + ", mid = " + mid + ", high = " + high);

            mergeSort(array, low, mid, comparator);
            mergeSort(array, mid + 1, high, comparator);
            merge(array, low, mid, high, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    protected void merge(E[] array, int low, int mid, int high, Comparator<E> comparator) {

        E[] aux = (E[]) new Object[high - low + 1];
        for (int k = low; k <= high; k++) {
            aux[k - low] = array[k];
        }
        int i = low;
        int j = mid + 1;
        for (int k = low; k <= high; k++) {

            if (i > mid) { // loop
                array[k] = aux[j - low];
                j++;
            } else if (j > high) { // loop
                array[k] = aux[i - low];
                i++;
            } else {
                if (comparator.compare(aux[i - low], aux[j - low]) <= 0) {
                    array[k] = aux[i - low];
                    i++;
                } else {
                    array[k] = aux[j - low];
                    j++;
                }
            }
        }
    }

}
