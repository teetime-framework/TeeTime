package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.concurrent.atomic.AtomicReference;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.signal.ISignal;

public abstract class InterThreadPipe extends AbstractPipe {

	private final AtomicReference<ISignal> signal = new AtomicReference<ISignal>();

	<T> InterThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void setSignal(final ISignal signal) {
		this.signal.lazySet(signal); // lazySet is legal due to our single-writer requirement
	}

	public ISignal getSignal() {
		return this.signal.get();
	}

	@Override
	public void reportNewElement() {
		// do nothing
	}
}
