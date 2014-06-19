package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractPipe<T> implements IPipe<T> {

	private final AtomicBoolean closed = new AtomicBoolean();

	@Override
	public boolean isClosed() {
		return this.closed.get();
	}

	@Override
	public void close() {
		this.closed.lazySet(true); // lazySet is legal due to our single-writer requirement
	}

}
