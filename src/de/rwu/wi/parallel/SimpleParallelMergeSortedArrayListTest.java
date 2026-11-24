package de.rwu.wi.parallel;

import de.rwu.wi.sequential.array.ASimpleArrayListSorterTest;
import de.rwu.wi.list.ISimpleList;

class SimpleParallelMergeSortedArrayListTest extends ASimpleArrayListSorterTest {
    @Override
    protected ISimpleList<Integer> getInstance(int n) {
        return new SimpleParallelMergeSortedArrayList<>(n);
    }

    @Override
    protected ISimpleList<String> getInstanceString(int n) {
        return new SimpleParallelMergeSortedArrayList(n);
    }
}