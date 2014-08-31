package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.util.concurrent.workstealing.CircularArray;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class OrderedGrowableArrayPipe<T> extends IntraThreadPipe<T> {

	private CircularArray<T> elements;
	private int head;
	private int tail;

	public OrderedGrowableArrayPipe() {
		this(1);
	}

	public OrderedGrowableArrayPipe(final int initialCapacity) {
		this.elements = new CircularArray<T>(initialCapacity);
	}

	@Deprecated
	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new OrderedGrowableArrayPipe<T>();
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final T element) {
		this.elements.put(this.tail++, element);
		return true;
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
