package de.rwu.wi.sequential.array;

import de.rwu.wi.list.ISimpleList;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;


public abstract class ASimpleArrayListSorterTest {

    protected ISimpleList<Integer> list;
    private Comparator<String> stringComparator = Comparator.comparing(String::toLowerCase);

    // ------------------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------------------

    protected abstract ISimpleList<Integer> getInstance(int n);

    protected abstract ISimpleList<String> getInstanceString(int n);


    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        list = getInstance(2000);
    }

    // ------------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------------


    @Test
    public void testSort1() {
        addRandomIntegers(15, 10);
        list.sort(Integer::compare);
        var size = isSorted();
        assertEquals(list.size(), size);
    }

    private void printList(ISimpleList<Integer> list) {
        for(int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + ", ");
        }
        System.out.println();
    }


    @Test
    public void testSort2() {
        addRandomIntegers(1000, 1000);
        list.sort(Integer::compare);
        var size = isSorted();
        assertEquals(list.size(), size);
    }

    protected void addRandomIntegers(int x, int scale) {
        for(int i = 0; i < x; i++) {
            var number = Double.valueOf(Math.random() * 10 * scale).intValue();
            list.add(number);
        }
    }

    protected int isSorted() {
        for(int i = 0; i < list.size() - 1; i++) {
            if(list.get(i) > list.get(i + 1)) {
                return i;
            }
        }
        return list.size();
    }

}
