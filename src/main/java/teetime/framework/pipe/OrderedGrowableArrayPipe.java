package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.util.concurrent.workstealing.CircularArray;

public final class OrderedGrowableArrayPipe extends IntraThreadPipe {

	private final CircularArray<Object> elements;
	private int head;
	private int tail;

	<T> OrderedGrowableArrayPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.elements = new CircularArray<Object>(capacity);
	}

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		IPipe pipe = new OrderedGrowableArrayPipe(sourcePort, targetPort, 4);
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
