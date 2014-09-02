package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.util.concurrent.workstealing.CircularArray;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class OrderedGrowableArrayPipe extends IntraThreadPipe {

	private final CircularArray<Object> elements;
	private int head;
	private int tail;

	public OrderedGrowableArrayPipe() {
		this(1);
	}

	public OrderedGrowableArrayPipe(final int initialCapacity) {
		this.elements = new CircularArray<Object>(initialCapacity);
	}

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		IPipe pipe = new OrderedGrowableArrayPipe();
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final Object element) {
		this.elements.put(this.tail++, element);
		return true;
	}

	@Override
	public Object removeLast() {
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
	public Object readLast() {
		return this.elements.get(this.head);
	}

	@Override
	public int size() {
		return this.tail - this.head;
	}

}
