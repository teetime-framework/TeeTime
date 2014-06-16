package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableResizableArrayQueue;

public class Pipe<T> {

	private final CommittableResizableArrayQueue<T> elements = new CommittableResizableArrayQueue<T>(null, 4);

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		Pipe<T> pipe = new Pipe<T>();
		sourcePort.pipe = pipe;
		targetPort.pipe = pipe;
	}

	public void add(final T element) {
		this.elements.addToTailUncommitted(element);
		this.elements.commit();
	}

	public T removeLast() {
		T element = this.elements.removeFromHeadUncommitted();
		this.elements.commit();
		return element;
	}

	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	public T readLast() {
		return this.elements.getTail();
	}

	public CommittableResizableArrayQueue<T> getElements() {
		return this.elements;
	}

}