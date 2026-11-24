package de.rwu.wi.sequential.linkedlist;

public class Node<E> {
	private Node<E> predecessor;
	private Node<E> successor;
	private E payload;

	public Node(E payload) {
		super();
		this.payload = payload;
	}

	public Node<E> getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Node<E> predecessor) {
		this.predecessor = predecessor;
	}

	public Node<E> getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node<E> successor) {
		this.successor = successor;
	}

	public E getPayload() {
		return payload;
	}

	public void setPayload(E payload) {
		this.payload = payload;
	}

}
