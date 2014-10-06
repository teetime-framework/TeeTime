package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class SingleElementPipe extends IntraThreadPipe {

	private Object element;

	<T> SingleElementPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Deprecated
	public static <T> void connect(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		IPipe pipe = new SingleElementPipe(null, null);
		pipe.connectPorts(sourcePort, targetPort);
	}

	@Override
	public boolean add(final Object element) {
		this.element = element;
		return true;
	}

	@Override
	public Object removeLast() {
		Object temp = this.element;
		this.element = null;
		return temp;
	}

	@Override
	public boolean isEmpty() {
		return this.element == null;
	}

	@Override
	public Object readLast() {
		return this.element;
	}

	@Override
	public int size() {
		return (this.element == null) ? 0 : 1;
	}

}
