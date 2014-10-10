package teetime.framework.signal;

import teetime.framework.AbstractStage;

public class StartingSignal implements ISignal {

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onStarting();
	}

}
