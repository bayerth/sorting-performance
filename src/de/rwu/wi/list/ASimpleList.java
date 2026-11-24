package de.rwu.wi.list;

import java.util.Collection;
import java.util.Comparator;

public abstract class ASimpleList<E> implements ISimpleList<E> {

    protected int currentSize = 0;
    protected boolean isSorted;

    public ASimpleList() {
        super();
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    @Override
    public boolean add(Collection<E> c) {
        boolean result = true;
        for(E e : c) {
            result = result && add(e);
        }
        return result;
    }

    @Override
    public boolean isSorted() {
        return isSorted;
    }

    public void sort(Comparator<E> comparator) {
        for(int n = size(); n > 1; n = n - 1) {
            boolean isSorted = true;
            for(int i = 0; i < n - 1; i = i + 1) {
                if(comparator.compare(get(i), get(i + 1)) > 0) {
                    isSorted = false;
                    E temp = get(i);
                    set(i, get(i + 1));
                    set(i + 1, temp);
                } // ende if
            } // ende innere for-Schleife
            if(isSorted) {
                break;
            } // ende if
        } // ende �u�ere for-Schleife
        isSorted = true;
    }

}