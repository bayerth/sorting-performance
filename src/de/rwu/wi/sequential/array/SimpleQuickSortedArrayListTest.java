package de.rwu.wi.sequential.array;

import de.rwu.wi.list.ISimpleList;

public class SimpleQuickSortedArrayListTest extends ASimpleArrayListSorterTest {

    @Override
    protected ISimpleList<Integer> getInstance(int n) {
        return new SimpleQuickSortedArrayList<Integer>(n);
    }

    @Override
    protected ISimpleList<String> getInstanceString(int n) {
        return new SimpleQuickSortedArrayList<String>(n);
    }
}