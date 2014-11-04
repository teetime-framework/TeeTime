package teetime.framework.signal;

import teetime.framework.AbstractStage;

public class TerminatingSignal implements ISignal {

	@Override
	public void trigger(final AbstractStage stage) {
		stage.onTerminating();
	}

}
