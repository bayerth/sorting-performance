package de.rwu.wi.sequential.array;


import de.rwu.wi.list.ISimpleList;

public class SimpleMergeSortedArrayListTest extends ASimpleArrayListSorterTest {

    @Override
    protected ISimpleList<Integer> getInstance(int n) {
        return new SimpleMergeSortedArrayList<Integer>(n);
    }

    @Override
    protected ISimpleList<String> getInstanceString(int n) {
        return new SimpleMergeSortedArrayList<String>(n);
    }

}
