package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;

public abstract class IntraThreadPipe extends AbstractPipe {

	@Override
	public void setSignal(final Signal signal) {
		if (this.getTargetPort() != null) {
			this.cachedTargetStage.onSignal(signal, this.getTargetPort());
		}
	}

	@Override
	public final void reportNewElement() {
		this.cachedTargetStage.executeWithPorts();
	}

}
