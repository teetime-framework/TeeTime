package teetime.variant.methodcallWithPorts.framework.core.signal;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;

public class TerminatingSignal implements ISignal {

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onTerminating();
	}

}
