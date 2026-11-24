# Sorting Performance

A Java project that compares list, array, merge-sort, quicksort, and parallel
sorting implementations. It includes JUnit-style exercises, performance demos,
and a Swing GUI module. Developed with support from GitHub Copilot.

## Modules

- `src/de/rwu/wi/sequential/array` - sequential array-based lists and sorters
- `src/de/rwu/wi/sequential/linkedlist` - sequential linked-list implementations
- `src/de/rwu/wi/parallel` - parallel sorters used by `MainPerformance`
- `src/de/rwu/wi/list` - shared list interfaces and abstractions
- `src/MainPerformance.java` - the performance comparison entry point
- `src/MainPerformanceGUI.java` and `src/de/rwu/wi/gui` - Swing performance comparison
- `src/deprecated` - local legacy implementations and demos (excluded by `.gitignore`)
- Tests are alongside their implementations or in `src/de/rwu/wi/tests`.
- `Sorter_GUI` - Swing-based GUI examples
- `test` - a small standalone Java test module
- `out` - generated IntelliJ output and JAR artifacts

## Requirements

- JDK 21 or newer
- IntelliJ IDEA or any Java compiler

## Build

Open the Maven project in IntelliJ IDEA. The main source root is `src`.

To compile the production sources from the command line:

```sh
javac -encoding UTF-8 -d out/production \
  $(find src Sorter_GUI/src -name '*.java' ! -name '*Test.java')
```

## Tests

Run the complete JUnit suite with Maven:

```sh
mvn test
```

The suite covers the array, linked-list, merge, quick and parallel sorting
implementations with ascending, descending, duplicate, empty, and
single-element cases. Legacy tests also run when `src/deprecated` is present.

## Run

The project has several entry points. For example:

```sh
java -cp out/production MainPerformance 10000
java -cp out/production MainPerformanceGUI
```

The performance GUI generates one input dataset for all selected algorithms.
Choose a positive list size, integers or fixed-length (10-character) strings,
select the algorithms, and press Start. The table shows elapsed sort time and
comparison count, or an error for an individual algorithm. Sorting runs in the
background; inputs and selection are disabled until the run finishes.
The lower half displays separate runtime and comparison bar charts for
completed algorithms; aborted or failed runs are omitted.
Each sorter runs on its own thread, separate from the Swing UI thread. Use
**Abort current** to request cooperative cancellation; the sorter and its
parallel workers terminate before the next selected algorithm runs on the same
input. An aborted row shows no time or comparison count.

Times exclude data generation and copying but include thread-safe comparison
counting. They are single, non-warmed-up measurements and are not a rigorous
benchmark. Bubble sorts are quadratic and can take a long time for large lists;
uncheck them when using large inputs. `MainPerformance` skips array bubble sort
above 100,000 elements and linked-list bubble sort above 50,000; these are
reference cutoffs, not guarantees of a short runtime. The GUI's Help menu
includes these list size guidelines. The older example GUIs are in
`Sorter_GUI/src/de/rwu/wi/oop`.

## Linked-list merge sort

`de.rwu.wi.sequential.linkedlist.LinkedListMergeSorter<E>` is a capacity-limited
`SimpleLinkedList` that sorts its nodes with a stable, iterative merge sort.
It takes O(n log n) time and O(1) auxiliary space.

```java
import de.rwu.wi.sequential.linkedlist.LinkedListMergeSorter;
import java.util.Comparator;

LinkedListMergeSorter<Integer> list = new LinkedListMergeSorter<>(100);
list.add(3);
list.add(1);
list.add(2);
list.sort(Comparator.naturalOrder());
```

## Parallel Performance

The parallel quicksort and merge-sort implementations use `ForkJoinPool` and
process ranges of the shared array concurrently. Each recursive task handles a
partition; partitions below 1,000 elements fall back to sequential sorting.
Merge-sort tasks merge their two sorted ranges after both complete.

A local benchmark with 1 million random integers produced approximately:

| Algorithm | Sequential | Parallel |
|---|---:|---:|
| QuickSort | 114 ms | 48-57 ms |
| MergeSort | 173 ms | 36-48 ms |

Results vary by processor, JVM, and system load. Parallel sorting is most
effective for larger lists; the thread-pool and task overhead may outweigh the
benefit for small inputs.

## Code Quality Notes

This project is intended for teaching sorting algorithms and Landau notation
(asymptotic complexity). The code is educational and is not intended for
production use.

## License

MIT License. See [LICENSE](LICENSE).

Copyright (c) 2026 Thomas Bayer
