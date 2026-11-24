import de.rwu.wi.sequential.array.SimpleArrayList;
import de.rwu.wi.sequential.array.SimpleMergeSortedArrayList;
import de.rwu.wi.sequential.array.SimpleQuickSortedArrayList;
import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.parallel.SimpleParallelMergeSortedArrayList;
import de.rwu.wi.parallel.SimpleParallelQuickSortedArrayList;
import de.rwu.wi.sequential.linkedlist.SimpleLinkedList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainPerformance {

    private static int size = 1000;
    private static long nrOfcomp = 0;
    private static Comparator<Integer> intComparator = (a, b) -> {
        nrOfcomp++;
        return Integer.compare(a, b);
    };
    private static DecimalFormat df = new DecimalFormat(".");

    private static Comparator<Integer> comparator = (a, b) -> {
        return Integer.compare(a, b);
    };
    public static final String CREATE_INFO = "create data: %,d, duration: %,d ms \n";
    public static final String COPY_INFO = "copy data: %,d elements, duration: %,d ms \n";
    public static final String NAME_INFO = "%s sort %,d elements \n";
    public static final String RESULT_INFO = "duration:  %,d ms, number of comp. = %,d \n";

    public static void main(String[] args) {
        if(args.length == 1) {
            try {
                size = Integer.parseInt(args[0]);
            } catch(Exception e) {
                System.out.println("parameter is not an integer");
                System.exit(-1);
            }
        }
        ISimpleList<Integer> arrayList, mergeSortedArrayList,
                paralellMergeSortedArrayList, quickSortedArrayList,
                parallelQuickSortedArrayList, linkedList;

        long time = System.currentTimeMillis();
        List<Integer> data = createData(size);
        time = System.currentTimeMillis() - time;

        System.out.format(CREATE_INFO, size, time);
        mergeSortedArrayList = new SimpleMergeSortedArrayList<>(size);
        sortPerformance(mergeSortedArrayList, data, intComparator, "MergeSort");

        paralellMergeSortedArrayList = new SimpleParallelMergeSortedArrayList<>(size);
        sortPerformance(paralellMergeSortedArrayList, data, intComparator, "Paralell MergeSort");

        quickSortedArrayList = new SimpleQuickSortedArrayList<>(size);
        sortPerformance(quickSortedArrayList, data, intComparator, "QuickSort");

        parallelQuickSortedArrayList = new SimpleParallelQuickSortedArrayList<>(size);
        sortPerformance(parallelQuickSortedArrayList, data, intComparator,
                        "Parallel QuickSort - 2 Threads");

        if(size <= 100000) {
            arrayList = new SimpleArrayList<>(size);
            sortPerformance(arrayList, data, intComparator, "BubbleSort");
        }
        if(size <= 50000) {
            linkedList = new SimpleLinkedList<>(size);
            sortPerformance(linkedList, data, intComparator, "LinkedList: BubbleSort");
        }
        System.out.println(
                "------------------------------------------------------------------------------------");
        System.out.format(NAME_INFO, "Java.util.ArrayList.sort", data.size());
        nrOfcomp = 0;
        time = System.currentTimeMillis();
        data.sort(intComparator);
        time = System.currentTimeMillis() - time;
        System.out.format(RESULT_INFO, time, nrOfcomp);
    }

    private static long sortPerformance(ISimpleList<Integer> list, List<Integer> data,
                                        Comparator<Integer> comparator, String name) {
        long time;
        nrOfcomp = 0;
        System.out.println(
                "------------------------------------------------------------------------------------");
        time = System.currentTimeMillis();

        list.add(data);
        time = System.currentTimeMillis() - time;

        System.out.format(COPY_INFO, data.size(), time);
        System.out.println(toPartialString(list));
        System.out.format(NAME_INFO, name, list.size());
        time = System.currentTimeMillis();
        list.sort(comparator);
        time = System.currentTimeMillis() - time;
        System.out.format(RESULT_INFO, time, nrOfcomp);
        System.out.println(toPartialString(list));
        return time;

    }

    protected static String toPartialString(ISimpleList<Integer> list) {
        String numbers = "[ ";
        for(int i = 0; i < 9; i++) {
            numbers += list.get(i) + ", ";
        }
        numbers += list.get(9) + " ] ";
        return numbers;
    }

    protected static List<Integer> createData(int size) {
        int numbers[] = new int[size];
        List<Integer> data = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            numbers[i] = Double.valueOf(Math.random() * 10 * size).intValue();
            data.add(numbers[i]);
        }
        return data;
    }
}
