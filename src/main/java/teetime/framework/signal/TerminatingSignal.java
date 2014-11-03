package teetime.framework.signal;

import teetime.framework.AbstractStage;

public class TerminatingSignal implements ISignal {

	@Override
	public void trigger(final AbstractStage stage) {
		try {
			stage.onTerminating();
		} catch (OnTerminatingException e) {
			throw new RuntimeException(e);
		}
	}

}
