package de.rwu.wi.sequential.linkedlist;

import java.util.Comparator;
import java.util.Objects;

/**
 * A stable merge-sorted list that rearranges its linked nodes in place.
 */
public class LinkedListMergeSorter<E> extends SimpleLinkedList<E> {

    public LinkedListMergeSorter() {
        super();
    }

    public LinkedListMergeSorter(int maxSize) {
        super(maxSize);
    }

    @Override
    public void sort(Comparator<E> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        isSorted = false;
        int size = size();
        for (int width = 1; width < size; width = width > size / 2 ? size : width * 2) {
            Node<E> nextRun = firstNode();
            int remaining = size;
            while (remaining > width) {
                int leftCount = width;
                int rightCount = Math.min(width, remaining - width);
                Node<E> left = nextRun;
                Node<E> right = advance(left, leftCount);
                nextRun = advance(right, rightCount);
                remaining -= leftCount + rightCount;

                while (leftCount > 0 && rightCount > 0) {
                    if (comparator.compare(left.getPayload(), right.getPayload()) <= 0) {
                        left = left.getSuccessor();
                        leftCount--;
                    } else {
                        Node<E> nextRight = right.getSuccessor();
                        moveBefore(right, left);
                        right = nextRight;
                        rightCount--;
                    }
                }
            }
        }
        isSorted = true;
    }

    private Node<E> advance(Node<E> node, int count) {
        for (int i = 0; i < count; i++) {
            node = node.getSuccessor();
        }
        return node;
    }
}
