package de.rwu.wi.tests;

import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.parallel.SimpleParallelMergeSortedArrayList;
import de.rwu.wi.parallel.SimpleParallelQuickSortedArrayList;
import de.rwu.wi.sequential.array.SimpleArrayList;
import de.rwu.wi.sequential.array.SimpleMergeSortedArrayList;
import de.rwu.wi.sequential.array.SimpleQuickSortedArrayList;
import de.rwu.wi.sequential.linkedlist.LinkedListMergeSorter;
import de.rwu.wi.sequential.linkedlist.SimpleLinkedList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortingImplementationsTest {
    private List<Supplier<ISimpleList<Integer>>> integerFactories() {
        return List.of(
                () -> new SimpleArrayList<>(100),
                () -> new SimpleLinkedList<>(100),
                () -> new LinkedListMergeSorter<>(100),
                () -> new SimpleMergeSortedArrayList<>(100),
                () -> new SimpleQuickSortedArrayList<>(100),
                () -> new SimpleParallelMergeSortedArrayList<>(100),
                () -> new SimpleParallelQuickSortedArrayList<>(100, 4));
    }

    @Test
    void sortsDuplicatesInAscendingOrder() {
        for (Supplier<ISimpleList<Integer>> factory : integerFactories()) {
            ISimpleList<Integer> list = factory.get();
            for (int value : new int[]{8, 3, 8, 1, 5, 3, 2, 5, 0, 4}) {
                assertTrue(list.add(value), list.getClass().getSimpleName());
            }
            list.sort(Comparator.naturalOrder());
            assertEquals(10, list.size(), list.getClass().getSimpleName());
            for (int i = 1; i < list.size(); i++) {
                assertTrue(list.get(i - 1) <= list.get(i), list.getClass().getSimpleName());
            }
            assertTrue(list.isSorted(), list.getClass().getSimpleName());
        }
    }

    @Test
    void handlesEmptyAndSingleElementLists() {
        for (Supplier<ISimpleList<Integer>> factory : integerFactories()) {
            ISimpleList<Integer> empty = factory.get();
            empty.sort(Comparator.naturalOrder());
            assertEquals(0, empty.size());
            ISimpleList<Integer> one = factory.get();
            assertTrue(one.add(42));
            one.sort(Comparator.naturalOrder());
            assertEquals(42, one.get(0));
            assertTrue(one.isSorted());
        }
    }

    @Test
    void supportsReverseStringOrder() {
        List<Supplier<ISimpleList<String>>> factories = new ArrayList<>();
        factories.add(() -> new SimpleArrayList<>(100));
        factories.add(() -> new SimpleLinkedList<>(100));
        factories.add(() -> new LinkedListMergeSorter<>(100));
        factories.add(() -> new SimpleMergeSortedArrayList<>(100));
        factories.add(() -> new SimpleQuickSortedArrayList<>(100));
        factories.add(() -> new SimpleParallelMergeSortedArrayList<>(100));
        factories.add(() -> new SimpleParallelQuickSortedArrayList<>(100, 4));
        for (Supplier<ISimpleList<String>> factory : factories) {
            ISimpleList<String> list = factory.get();
            list.add("beta");
            list.add("Alpha");
            list.add("beta");
            list.add("gamma");
            list.sort(Comparator.reverseOrder());
            assertEquals("gamma", list.get(0));
            assertEquals("beta", list.get(1));
            assertEquals("beta", list.get(2));
            assertEquals("Alpha", list.get(3));
        }
    }
}
