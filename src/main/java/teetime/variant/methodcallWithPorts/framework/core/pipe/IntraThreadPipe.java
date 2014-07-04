package teetime.variant.methodcallWithPorts.framework.core.pipe;

import teetime.variant.methodcallWithPorts.framework.core.Signal;

public abstract class IntraThreadPipe<T> extends AbstractPipe<T> {

	@Override
	public void setSignal(final Signal signal) {
		this.getTargetPort().getOwningStage().onSignal(signal, this.getTargetPort());
	}

}
