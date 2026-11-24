package de.rwu.wi.list;

/**
 * L�sungsvorschlag UE1 und UE2
 * 2018 Sommersemester, OOPP
 * Thomas Bayer
 */

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public abstract class SimpleListTest {

    private ISimpleList<Integer> l;
    private Comparator<String> stringComparator = Comparator.comparing(String::toLowerCase);

    // ------------------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------------------

    protected abstract ISimpleList<Integer> getInstance(int n);

    protected abstract ISimpleList<String> getInstanceString(int n);


    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        l = getInstance(5);
    }

    // ------------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------------

    @Test
    public void testSize1() {
        int actual = l.size();
        assertEquals(0, actual);
    }

    @Test
    public void testSize2() {
        l.add(1);
        l.add(2);
        int actual = l.size();
        assertEquals(2, actual);
    }

    @Test
    public void testIsEmpty1() {
        boolean actual = l.isEmpty();
        assertEquals(true, actual);
    }

    @Test
    public void testIsEmpty2() {
        l.add(9);
        boolean actual = l.isEmpty();
        assertEquals(false, actual);
    }

    @Test
    public void testAddInt1() {
        boolean actual = l.add(1);
        assertEquals(true, actual);
    }

    @Test
    public void testAddInt2() {
        l.add(4);
        l.add(4);
        l.add(4);
        l.add(4);
        boolean actual = l.add(1);
        assertEquals(true, actual);
    }

    @Test
    public void testAddInt3() {
        l.add(4);
        l.add(4);
        l.add(4);
        l.add(4);
        l.add(5);
        boolean actual = l.add(1);
        assertEquals(false, actual);
    }

    @Test
    public void testRemove1() {
        boolean actual = l.remove(new Integer(1));
        assertEquals(false, actual);
    }

    @Test
    public void testRemove2() {
        l.add(20);
        l.add(30);
        boolean actual = l.remove(new Integer(20));
        assertEquals(true, actual);
    }

    @Test
    public void testRemove3() {
        l.add(20);
        l.add(30);
        l.remove(new Integer(20));
        int actual = l.size();
        assertEquals(1, actual);
    }

    @Test
    public void testRemove4() {
        l.add(20);
        l.add(30);
        l.remove(new Integer(20));
        boolean actual = l.contains(20);
        assertEquals(false, actual);
    }

    @Test
    public void testRemove5() {
        l.add(20);
        l.add(30);
        l.remove(new Integer(20));
        int actual = l.get(0);
        assertEquals(30, actual);
    }

    @Test
    public void testRemove6() {
        l.add(20);
        boolean actual = l.remove(null);
        assertEquals(false, actual);
    }

    @Test
    public void testRemove7() {
        l.add(10);
        boolean actual = l.remove(0);
        assertEquals(true, actual);
    }

    @Test
    public void testRemove8() {
        l.add(10);
        l.add(20);
        l.remove(0);
        boolean actual = l.contains(10);
        assertEquals(false, actual);
    }

    @Test
    public void testRemove9() {
        l.add(10);
        l.add(20);
        l.remove(0);
        int actual = l.size();
        assertEquals(1, actual);
    }

    @Test
    public void testContains1() {
        boolean actual = l.contains(new Integer(1));
        assertEquals(false, actual);
    }

    @Test
    public void testContains2() {
        l.add(20);
        l.add(30);
        boolean actual = l.contains(new Integer(20));
        assertEquals(true, actual);
    }

    @Test
    public void testAddIntInt1() {
        l.add(10);
        l.add(20);
        l.add(1, 30);
        int actual = l.get(1);
        assertEquals(30, actual);
        actual = l.get(2);
        assertEquals(20, actual);
    }

    @Test
    public void testGet() {
        try {
            l.get(0);
            fail("No exception");
        } catch(Exception e) {
        }

    }

    @Test
    public void testSet1() {
        l.add(10);
        l.add(20);
        l.set(1, 30);
        int actual = l.get(1);
        assertEquals(30, actual);
        actual = l.size();
        assertEquals(2, actual);
    }

    @Test
    public void testSubList1() {
        l.add(1);
        IndexOutOfBoundsException actual = null;
        try {
            l.subList(1, 0);
        } catch(IndexOutOfBoundsException e) {
            actual = e;
        } finally {
            if(actual == null) {
                fail();
            }
        }

    }

    @Test
    public void testSubList2() {
        add4Elements();
        ISimpleList<Integer> actual = l.subList(0, 0);
        assertEquals(1, actual.size());
        assertEquals(new Integer(10), actual.get(0));
    }

    @Test
    public void testSubList3() {
        add4Elements();
        ISimpleList<Integer> actual = l.subList(0, 2);
        assertEquals(3, actual.size());
        assertEquals(new Integer(10), actual.get(0));
        assertEquals(new Integer(20), actual.get(1));
        assertEquals(new Integer(30), actual.get(2));
    }

    @Test
    public void testSubList4() {
        add4Elements();
        ISimpleList<Integer> actual = l.subList(2, 3);
        assertEquals(2, actual.size());
        assertEquals(new Integer(30), actual.get(0));
        assertEquals(new Integer(40), actual.get(1));
    }

    @Test
    public void testSort1() {
        l.add(20);
        l.add(10);
        l.add(40);
        l.add(30);
        l.sort(Integer::compare);
        assertEquals(new Integer(10), l.get(0));
        assertEquals(new Integer(20), l.get(1));
        assertEquals(new Integer(30), l.get(2));
        assertEquals(new Integer(40), l.get(3));
    }

    @Test
    public void testSort5() {
        for(int i = 0; i < 1000; i++) {
            var number = Double.valueOf(Math.random() * 10 * 1000).intValue();
            l.add(number);
        }
        l.sort(Integer::compare);
        for(int i = 0; i < l.size() - 1; i++) {
            if(l.get(i) > l.get(i + 1)) {
                assertEquals(i, i + 1);
            }
        }
        assertEquals(true, true);
    }


    @Test
    public void addCollection1() {
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        boolean actual = l.add(intList);
        assertEquals(true, actual);
    }

    @Test
    public void addCollection2() {
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        l.add(intList);
        boolean actual = l.contains(1);
        assertEquals(true, actual);
    }

    @Test
    public void addCollection3() {
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(4);
        intList.add(1);
        l.add(intList);
        int actual = l.size();
        assertEquals(4, actual);
    }


    @Test
    public void testSort1a() {
        l.add(4);
        l.add(2);
        l.add(1);
        l.add(3);
        l.sort(Integer::compare);
        assertEquals(new Integer(1), l.get(0));
        assertEquals(new Integer(2), l.get(1));
        assertEquals(new Integer(3), l.get(2));
        assertEquals(new Integer(4), l.get(3));
    }

    @Test
    public void testSort3a() {
        l.add(4);
        l.add(1);
        l.add(2);
        l.add(3);
        l.sort(Integer::compare);
        assertEquals(new Integer(1), l.get(0));
        assertEquals(new Integer(2), l.get(1));
        assertEquals(new Integer(3), l.get(2));
        assertEquals(new Integer(4), l.get(3));
    }

    @Test
    public void testSort2() {
        ISimpleList<String> strings = getInstanceString(5);
        strings.add("OOP");
        strings.add("OOPP");
        strings.add("ABAP");
        strings.add("JEE");
        strings.sort(stringComparator);
        assertEquals("ABAP", strings.get(0));
        assertEquals("JEE", strings.get(1));
        assertEquals("OOP", strings.get(2));
        assertEquals("OOPP", strings.get(3));
    }

    @Test
    public void testIsSorted1() {
        boolean actual = l.isSorted();
        assertEquals(true, actual);
    }

    @Test
    public void testIsSorted2() {
        l.add(2);
        l.add(1);
        boolean actual = l.isSorted();
        assertEquals(false, actual);
    }

    @Test
    public void testIsSorted3() {
        l.add(2);
        l.add(1);
        l.sort((x, y) -> {
            return Integer.compare(x, y);
        });
        boolean actual = l.isSorted();
        assertEquals(true, actual);
    }

    @Test
    public void testIsSorted4() {
        l.add(2);
        l.add(1);
        l.sort(Integer::compare);
        l.add(0);
        boolean actual = l.isSorted();
        assertEquals(false, actual);
    }


    @Test
    public void testGet1() {
        l.add(2);
        l.add(1);
        l.add(3);
        Integer actual = l.get(0);
        assertEquals((Object) 2, (Object) actual);
        actual = l.get(2);
        assertEquals((Object) 3, (Object) actual);
        actual = l.get(1);
        assertEquals((Object) 1, (Object) actual);
    }

    // @Test
    // public void testIsSorted5() {
    // l.add(2);
    // l.add(1);
    // l.sort((x, y) -> {
    // return Integer.compare(x, y);
    // });
    // l.add(5);
    // boolean actual = l.isSorted();
    // assertEquals(true, actual);
    // }

    // ------------------------------------------------------------------------------
    // Support
    // ------------------------------------------------------------------------------
    protected void add4Elements() {
        l.add(10);
        l.add(20);
        l.add(30);
        l.add(40);
    }
}
