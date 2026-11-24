package de.rwu.wi.sequential.array;

import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.list.SimpleListTest;

public class SimpleArrayListTest extends SimpleListTest {

	@Override
	protected ISimpleList<Integer> getInstance(int n) {
		return new SimpleArrayList<Integer>(n);
	}

	@Override
	protected ISimpleList<String> getInstanceString(int n) {
		return new SimpleArrayList<String>(n);
	}

}
