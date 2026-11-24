package de.rwu.wi.parallel;

import de.rwu.wi.sequential.array.ASimpleArrayListSorterTest;
import de.rwu.wi.list.ISimpleList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleParallelQuickSortedArrayListTest extends ASimpleArrayListSorterTest {
    @Override
    protected ISimpleList<Integer> getInstance(int n) {
        return new SimpleParallelQuickSortedArrayList<>(n);
    }

    @Override
    protected ISimpleList<String> getInstanceString(int n) {
        return new SimpleParallelQuickSortedArrayList(n);
    }

    @Test
    public void testSort3() {
        int n = 1000000;
        addRandomIntegers(n, n);
        list.sort(Integer::compare);
        var size = isSorted();
        assertEquals(list.size(), size);
    }
}