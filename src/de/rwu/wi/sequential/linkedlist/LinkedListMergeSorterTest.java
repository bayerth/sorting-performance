package de.rwu.wi.sequential.linkedlist;

import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.list.SimpleListTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkedListMergeSorterTest extends SimpleListTest {
    @Override
    protected ISimpleList<Integer> getInstance(int n) {
        return new LinkedListMergeSorter<>(n);
    }

    @Override
    protected ISimpleList<String> getInstanceString(int n) {
        return new LinkedListMergeSorter<>(n);
    }

    @Test
    void sortsRandomInputsWithoutLosingElements() {
        Random random = new Random(42);
        for (int size = 0; size <= 257; size++) {
            LinkedListMergeSorter<Integer> list = new LinkedListMergeSorter<>(size + 1);
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int value = random.nextInt(31) - 15;
                list.add(value);
                expected.add(value);
            }
            expected.sort(Comparator.naturalOrder());

            list.sort(Comparator.naturalOrder());

            assertEquals(size, list.size());
            for (int i = 0; i < size; i++) {
                assertEquals(expected.get(i), list.get(i), "size " + size + ", index " + i);
            }
        }
    }

    @Test
    void retainsOrderOfEqualKeys() {
        record Item(int key, String name) {}
        LinkedListMergeSorter<Item> list = new LinkedListMergeSorter<>(6);
        list.add(new Item(2, "first"));
        list.add(new Item(1, "second"));
        list.add(new Item(2, "third"));
        list.add(new Item(1, "fourth"));
        list.add(new Item(2, "fifth"));
        list.add(new Item(1, "sixth"));

        list.sort(Comparator.comparingInt(Item::key));

        assertEquals(List.of("second", "fourth", "sixth", "first", "third", "fifth"),
                List.of(list.get(0).name(), list.get(1).name(), list.get(2).name(),
                        list.get(3).name(), list.get(4).name(), list.get(5).name()));
    }

    @Test
    void supportsMutationsAndBackwardsTraversalAfterSorting() {
        LinkedListMergeSorter<Integer> list = new LinkedListMergeSorter<>(8);
        for (int value : new int[]{5, 1, 4, 2, 3}) {
            list.add(value);
        }
        list.get(2);
        list.sort(Comparator.naturalOrder());
        assertEquals(5, list.get(4));
        assertEquals(4, list.get(3));
        assertTrue(list.remove(0));
        assertTrue(list.remove(list.size() - 1));
        list.add(0, 0);
        assertTrue(list.add(6));
        assertEquals(List.of(0, 2, 3, 4, 6),
                List.of(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4)));
        list.sort(Comparator.reverseOrder());
        assertEquals(List.of(6, 4, 3, 2, 0),
                List.of(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4)));
    }

    @Test
    void comparatorFailureLeavesAllNodesAccessible() {
        LinkedListMergeSorter<Integer> list = new LinkedListMergeSorter<>(6);
        for (int value : new int[]{5, 4, 3, 2, 1, 0}) {
            list.add(value);
        }
        AtomicInteger comparisons = new AtomicInteger();
        assertThrows(IllegalStateException.class, () -> list.sort((a, b) -> {
            if (comparisons.incrementAndGet() == 5) {
                throw new IllegalStateException("comparison failed");
            }
            return Integer.compare(a, b);
        }));
        assertFalse(list.isSorted());
        assertEquals(6, list.size());
        list.sort(Comparator.naturalOrder());
        for (int i = 0; i < 6; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    void comparisonsGrowAtMostNLogN() {
        int size = 1024;
        LinkedListMergeSorter<Integer> list = new LinkedListMergeSorter<>(size);
        for (int i = size - 1; i >= 0; i--) {
            list.add(i);
        }
        AtomicInteger comparisons = new AtomicInteger();
        list.sort((a, b) -> {
            comparisons.incrementAndGet();
            return Integer.compare(a, b);
        });
        assertTrue(comparisons.get() <= size * 10);
        for (int i = 0; i < size; i++) {
            assertEquals(i, list.get(i));
        }
    }
}
