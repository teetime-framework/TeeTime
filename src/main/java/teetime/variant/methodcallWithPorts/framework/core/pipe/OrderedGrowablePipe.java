package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.LinkedList;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public class OrderedGrowablePipe<T> extends IntraThreadPipe<T> {

	private LinkedList<T> elements;

	public OrderedGrowablePipe() {
		this(100000);
	}

	public OrderedGrowablePipe(final int initialCapacity) {
		this.elements = new LinkedList<T>();
	}

	@Deprecated
	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new OrderedGrowablePipe<T>();
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final T element) {
		return this.elements.offer(element);
	}

	@Override
	public T removeLast() {
		return this.elements.poll();
	}

	@Override
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	@Override
	public T readLast() {
		return this.elements.peek();
	}

	@Override
	public int size() {
		return this.elements.size();
	}

}
