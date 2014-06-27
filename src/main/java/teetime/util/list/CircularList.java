package teetime.util.list;

public final class CircularList<T> {

	private static final class Node<T> {
		T value;
		Node<T> next;
	}

	private Node<T> headNode;
	private Node<T> lastNode;

	private Node<T> currentNode;

	public void add(final T value) {
		Node<T> newNode = new Node<T>();
		newNode.value = value;

		if (this.headNode == null) { // newNode is the first node
			this.headNode = this.lastNode = newNode;
			this.currentNode = newNode;
		}

		this.lastNode.next = newNode;
		newNode.next = this.headNode;
	}

	public T getNext() {
		T value = this.currentNode.value;
		this.currentNode = this.currentNode.next;
		return value;
	}
}
