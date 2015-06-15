package teetime.framework.pipe;

import teetime.framework.AbstractIntraThreadPipe;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class InstantiationPipe<T> extends AbstractIntraThreadPipe {

	private final InputPort<T> target;
	private final int capacity;

	public InstantiationPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		super(sourcePort, targetPort);
		this.target = targetPort;
		this.capacity = capacity;
		sourcePort.setPipe(this);
	}

	public int getCapacity() {
		return capacity;
	}

	public InputPort<T> getTarget() {
		return target;
	}

	@Override
	public boolean add(final Object element) {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public boolean isEmpty() {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public int size() {
		throw new IllegalStateException("Should not be called");
	}

	@Override
	public Object removeLast() {
		throw new IllegalStateException("Should not be called");
	}

}
