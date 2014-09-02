package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicReference;

import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;

public abstract class InterThreadPipe extends AbstractPipe {

	private final AtomicReference<Signal> signal = new AtomicReference<Signal>();

	@Override
	public void setSignal(final Signal signal) {
		this.signal.lazySet(signal); // lazySet is legal due to our single-writer requirement
	}

	public Signal getSignal() {
		return this.signal.get();
	}

	@Override
	public void reportNewElement() {
		// do nothing
	}
}
