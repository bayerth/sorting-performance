package de.rwu.wi.sequential.array;


import de.rwu.wi.list.ASimpleList;
import de.rwu.wi.list.ISimpleList;

public class SimpleArrayList<E> extends ASimpleList<E> {

	protected E[] array;
	protected static final int MAX_SIZE = 10;

	// ------------------------------------------------------------------------------
	// Konstruktoren
	// ------------------------------------------------------------------------------
	public SimpleArrayList() {
		init(MAX_SIZE);
	};

	public SimpleArrayList(int size) {
		init(size);
	}

	@SuppressWarnings("unchecked")
	protected void init(int size) {
		size = size <= 0 ? MAX_SIZE : size; // Fragezeichen-Operator
		array = (E[]) new Object[size];
		isSorted = true;
	};

	// ------------------------------------------------------------------------------
	// API
	// ------------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#add(E)
	 */
	@Override
	public boolean add(E e) {
		if (currentSize >= array.length) {
			return false;
		}
		isSorted = false;
		array[currentSize] = e;
		currentSize++;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		boolean result = false;
		if (o != null) {
			for (int i = 0; i < currentSize; i++) {
				if (o.equals(array[i])) {
					remove(i);
					return true;
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < currentSize; i++) {
			if (o == null ? array[i] == null : o.equals(array[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#add(int, E)
	 */
	@Override
	public void add(int i, E e) throws IndexOutOfBoundsException {
		if (i < 0 || i > currentSize || currentSize == array.length) {
			throw new IndexOutOfBoundsException();
		}
		shiftRight(i);
		isSorted = false;
		array[i] = e;
		currentSize++;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#get(int)
	 */
	@Override
	public E get(int i) throws IndexOutOfBoundsException {
		if (i < 0 || i >= currentSize) {
			throw new IndexOutOfBoundsException();
		}
		return array[i];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#set(int, E)
	 */
	@Override
	public int set(int i, E e) throws IndexOutOfBoundsException {
		if (i < 0 || i >= currentSize) {
			throw new IndexOutOfBoundsException();
		}
		array[i] = e;
		isSorted = false;
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#remove(int)
	 */
	@Override
	public boolean remove(int i) {
		if (i < 0 || i >= currentSize) {
			return false;
		}
		shiftLeft(i);
		array[currentSize - 1] = null;
		currentSize--;
		isSorted = false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see impl.ISimpleList#subList(int, int)
	 */
	@Override
	public ISimpleList<E> subList(int i, int j) throws IndexOutOfBoundsException {
		if (i < 0 || i >= currentSize || j < 0 || j >= currentSize) {
			throw new IndexOutOfBoundsException();
		}
		ISimpleList<E> result = new SimpleArrayList<>(Math.max(0, j - i + 1));
		if (i > j) {
			return result;
		}
		for (int n = i; n <= j; n++) {
			result.add(array[n]);
		}
		return result;
	}

	private void shiftRight(int index) {
		for (int i = currentSize; i > index; i--) {
			array[i] = array[i - 1];
		}
	}

	private void shiftLeft(int index) {
		for (int i = index; i < currentSize; i++) {
			array[i] = array[i + 1];
		}
	}

}
