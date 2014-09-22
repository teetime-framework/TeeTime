package teetime.variant.methodcallWithPorts.framework.core.signal;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;

public class StartingSignal implements ISignal {

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onStarting();
	}

}
