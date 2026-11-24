package de.rwu.wi.sequential.linkedlist;

import de.rwu.wi.list.ASimpleList;
import de.rwu.wi.list.ISimpleList;

import java.util.Comparator;

public class SimpleLinkedList<E> extends ASimpleList<E> {
    private int size;
    private int maxSize;
    private Node<E> first;
    private Node<E> last;

    private Node<E> currentNode;
    private int currentIndex;

    public SimpleLinkedList() {
        super();
        init(100);
    }

    public SimpleLinkedList(int maxSize) {
        super();
        init(maxSize);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E e) {
        if (e == null || size >= maxSize) {
            return false;
        }
        Node<E> newNode = new Node<>(e);
        if (first == null) {
            first = newNode;
        }
        else {
            Node<E> currentNode = getNode(size - 1); // last node
            insertNode(currentNode, newNode);
        }
        size++;
        isSorted = size <= 1;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        Node<E> node = findNode(o);
        if (node == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        Node<E> node = first;
        int index = 0;
        while (node != null) {
            if (o.equals(node.getPayload())) {
                return remove(index);
            }
            node = node.getSuccessor();
            index++;
        }
        return false;
    }

    // @Override
    // public void forAll(ICommand command) {
    // if (command != null) {
    // Node<E> node = first;
    // while (node != null) {
    // command.execute(node.getPayload());
    // node = node.getSuccessor();
    // }
    // }
    // }

    private Node<E> findNode(Object o) {
        Node<E> node = first;
        boolean loop = true;
        while (loop) {
            if (node == null) {
                loop = false;
            }
            else {
                if (o == null ? node.getPayload() == null : node.getPayload().equals(o)) {
                    loop = false;
                }
                else {
                    node = node.getSuccessor();
                }
            }
        }
        return node;
    }

    // nur T90

    @Override
    public void add(int i, E e) throws IndexOutOfBoundsException {
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException();
        }
        if (size >= maxSize) {
            throw new IllegalStateException("List is full");
        }

        Node<E> newNode = new Node<>(e);
        if (i == 0) {
            if (first == null) {
                first = newNode;
            }
            else {
                newNode.setSuccessor(first);
                first.setPredecessor(newNode);
                first = newNode;
            }
        }
        else { // i > 0
            Node<E> currentNode = getNode(i - 1);
            insertNode(currentNode, newNode);
        }
        size++;
        currentNode = first;
        currentIndex = 0;
        isSorted = false;
    }

    protected final Node<E> firstNode() {
        return first;
    }

    protected final void moveBefore(Node<E> node, Node<E> before) {
        Node<E> previous = node.getPredecessor();
        Node<E> next = node.getSuccessor();
        previous.setSuccessor(next);
        if (next != null) {
            next.setPredecessor(previous);
        } else {
            last = previous;
        }

        Node<E> beforePrevious = before.getPredecessor();
        node.setPredecessor(beforePrevious);
        node.setSuccessor(before);
        before.setPredecessor(node);
        if (beforePrevious == null) {
            first = node;
        } else {
            beforePrevious.setSuccessor(node);
        }
        currentNode = first;
        currentIndex = 0;
    }

    private void insertNode(Node<E> currentNode, Node<E> newNode) {
        if (currentNode.getSuccessor() != null) { // in the middle
            Node<E> successor = currentNode.getSuccessor();
            newNode.setSuccessor(successor);
            successor.setPredecessor(newNode);
        }
        currentNode.setSuccessor(newNode);
        newNode.setPredecessor(currentNode);
    }

    @Override
    public E get(int i) throws IndexOutOfBoundsException {
        return getNode(i).getPayload();
    }

    @Override
    public int set(int i, E e) throws IndexOutOfBoundsException {
        Node<E> currentNode = getNode(i);
        if (currentNode != null) {
            currentNode.setPayload(e);
        }
        isSorted = false;
        return i;
    }

    @Override
    public boolean remove(int i) {
        if (i < 0 || i >= size) {
            return false;
        }
        Node<E> removed = getNode(i);
        Node<E> predecessor = removed.getPredecessor();
        Node<E> successor = removed.getSuccessor();
        if (predecessor == null) {
            first = successor;
        } else {
            predecessor.setSuccessor(successor);
        }
        if (successor != null) {
            successor.setPredecessor(predecessor);
        }
        if (successor == null) {
            last = predecessor;
        }
        size--;
        if (size == 0) {
            first = null;
            last = null;
        }
        currentIndex = 0;
        currentNode = first;
        if (size == 0) {
            currentNode = null;
            currentIndex = -1;
        }
        isSorted = false;
        return true;
    }

    protected void removeNode(Node<E> currentNode) {
        Node<E> predecessor = currentNode.getPredecessor();
        Node<E> successor = currentNode.getSuccessor();
        if (predecessor != null) {
            predecessor.setSuccessor(successor);
        }
        if (successor != null) {
            successor.setPredecessor(predecessor);
        }
    }

    @Override
    public void sort(Comparator<E> comparator) {
        // TODO Auto-generated method stub
        super.sort(comparator);
    }

    private Node<E> getNode(int i) throws IndexOutOfBoundsException {
        if (i >= size || i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i == currentIndex) { // direct hit
            return currentNode;
        }
        // optimize for Sorting (access i and neighbour consecutively)
        if (i == currentIndex + 1) { // need right neighbour
            currentNode = currentNode.getSuccessor();
            currentIndex = i;
            return currentNode;
        }
        if (i == currentIndex - 1) { // need left neighbour
            currentNode = currentNode.getPredecessor();
            currentIndex = i;
            return currentNode;
        }


        Node<E> node = first;
        boolean forward = true;
        int j = 0;
//        if (currentNode != null) {
//
//            int distance = Math.abs(currentIndex - i);
//            if (distance < i) { // optimize
//                node = currentNode
//                if (currentIndex - i < 0) {
//                    forward = false;
//
//                }
//            }
//        }

        // optimize access
        // int distance = Math.abs(currentIndex - i);
        // int middle = size / 2;
        // if (i < middle) { // first half
        // if (distance < i) {
        // j = currentIndex;
        // node = currentNode;
        // }
        // }
        // else { // second half
        // int d2 = Math.abs(i - size);
        // if (distance < d2) { // optimization
        // j = currentIndex;
        // node = currentNode;
        // }
        // else {
        // j = size - 1;
        // node = this.last;
        // }
        // }
        if (forward) {
            while (j < i) {
                node = node.getSuccessor();
                j++;
            }
        }
        else { // backward
            while (j > i) {
                node = node.getPredecessor();
                j--;
            }
        }
        this.currentIndex = i;
        this.currentNode = node;
        return node;
    }

    @Override
    public ISimpleList<E> subList(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || j < i || j >= size) {
            throw new IndexOutOfBoundsException();
        }
        ISimpleList<E> result = new SimpleLinkedList<>(j - i + 1);
        for (int index = i; index <= j; index++) {
            result.add(get(index));
        }
        return result;
    }

    protected void init(int maxSize) {
        size = 0;
        this.maxSize = maxSize;
        isSorted = true;
        first = null;
        this.currentIndex = -maxSize - 1;
        this.currentNode = null;
    }
}
