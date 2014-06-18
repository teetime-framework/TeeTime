package teetime.examples.throughput.methodcall;

import teetime.util.concurrent.workstealing.CircularArray;

public class OrderedGrowableArrayPipe<T> extends AbstractPipe<T> {

	private CircularArray<T> elements;
	private int head;
	private int tail;

	public OrderedGrowableArrayPipe() {
		this(17);
	}

	public OrderedGrowableArrayPipe(final int initialCapacity) {
		this.elements = new CircularArray<T>(initialCapacity);
	}

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new OrderedGrowableArrayPipe<T>();
		sourcePort.setPipe(pipe);
		targetPort.setPipe(pipe);
	}

	@Override
	public void add(final T element) {
		this.elements.put(this.tail++, element);
	}

	@Override
	public T removeLast() {
		if (this.head < this.tail) {
			return this.elements.get(this.head++);
		} else {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public T readLast() {
		return this.elements.get(this.head);
	}

	@Override
	public int size() {
		return this.tail - this.head;
	}

}
