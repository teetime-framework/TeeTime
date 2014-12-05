package teetime.framework;

import teetime.framework.signal.ISignal;

public abstract class AbstractBasicStage implements IStage {

	protected abstract void executeWithPorts();

	protected abstract void onSignal(ISignal signal, InputPort<?> inputPort);

	protected abstract TerminationStrategy getTerminationStrategy();

	protected abstract void terminate();

	protected abstract boolean shouldBeTerminated();
}
