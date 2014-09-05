package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;

public abstract class IntraThreadPipe extends AbstractPipe {

	<T> IntraThreadPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		super(sourcePort, targetPort);
	}

	@Override
	public void setSignal(final Signal signal) {
		if (this.getTargetPort() != null) { // BETTER remove this check since there are DummyPorts
			this.cachedTargetStage.onSignal(signal, this.getTargetPort());
		}
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeWithPorts();
	}

}
