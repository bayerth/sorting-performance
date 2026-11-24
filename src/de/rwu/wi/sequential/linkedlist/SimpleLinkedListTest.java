package de.rwu.wi.sequential.linkedlist;

import de.rwu.wi.list.ISimpleList;
import de.rwu.wi.list.SimpleListTest;

public class SimpleLinkedListTest extends SimpleListTest {

	@Override
	protected ISimpleList<Integer> getInstance(int n) {
		return new SimpleLinkedList<Integer>(n);
	}

	@Override
	protected ISimpleList<String> getInstanceString(int n) {
		return new SimpleLinkedList<String>(n);
	}

}