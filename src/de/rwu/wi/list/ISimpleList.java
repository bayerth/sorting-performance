package de.rwu.wi.list;

import java.util.Collection;
import java.util.Comparator;

public interface ISimpleList<E> {

	int size();

	boolean isEmpty();

	boolean add(E e);

	boolean add(Collection<E> c);

	boolean remove(Object o);

	boolean contains(Object o);

	void add(int i, E e) throws IndexOutOfBoundsException;

	E get(int i) throws IndexOutOfBoundsException;

	int set(int i, E e) throws IndexOutOfBoundsException;

	boolean remove(int i);

	ISimpleList<E> subList(int i, int j) throws IndexOutOfBoundsException;

	void sort(Comparator<E> comparator);

	boolean isSorted();

}