package teetime.framework;

import teetime.framework.signal.ISignal;

public abstract class AbstractIntraThreadPipe extends AbstractPipe {

	protected <T> AbstractIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public final void sendSignal(final ISignal signal) {
		// getTargetPort is always non-null since the framework adds dummy ports if necessary
		this.cachedTargetStage.onSignal(signal, this.getTargetPort());
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeWithPorts();
	}

}
