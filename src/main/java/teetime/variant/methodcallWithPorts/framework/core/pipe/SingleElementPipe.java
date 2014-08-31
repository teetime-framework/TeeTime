package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public final class SingleElementPipe<T> extends IntraThreadPipe<T> {

	private T element;

	SingleElementPipe() {
		super();
	}

	@Deprecated
	public static <T> void connect(final OutputPort<T> sourcePort, final InputPort<T> targetPort) {
		IPipe<T> pipe = new SingleElementPipe<T>();
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final T element) {
		this.element = element;
		return true;
	}

	@Override
	public T removeLast() {
		T temp = this.element;
		this.element = null;
		return temp;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public T readLast() {
		return this.element;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

}
