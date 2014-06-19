package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.LinkedList;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public class OrderedGrowablePipe<T> extends AbstractPipe<T> {

	private LinkedList<T> elements;

	public OrderedGrowablePipe() {
		this(100000);
	}

	public OrderedGrowablePipe(final int initialCapacity) {
		this.elements = new LinkedList<T>();
	}

	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new OrderedGrowablePipe<T>();
		sourcePort.setPipe(pipe);
		targetPort.setPipe(pipe);
	}

	@Override
	public void add(final T element) {
		this.elements.offer(element);
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
