package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicBoolean;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public abstract class AbstractPipe<T> implements IPipe<T> {

	private final AtomicBoolean closed = new AtomicBoolean();
	private InputPort<T> targetPort;

	@Override
	public boolean isClosed() {
		return this.closed.get();
	}

	@Override
	public void close() {
		this.closed.lazySet(true); // lazySet is legal due to our single-writer requirement
	}

	@Override
	public InputPort<T> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public void setTargetPort(final InputPort<T> targetPort) {
		this.targetPort = targetPort;
	}

}
