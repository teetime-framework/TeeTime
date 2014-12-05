package teetime.framework;

import teetime.framework.pipe.AbstractPipe;
import teetime.framework.signal.ISignal;

public abstract class AbstractIntraThreadPipe extends AbstractPipe {

	protected <T> AbstractIntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void sendSignal(final ISignal signal) {
		if (this.getTargetPort() != null) { // BETTER remove this check since there are DummyPorts
			this.cachedTargetStage.onSignal(signal, this.getTargetPort());
		}
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeWithPorts();
	}

}
