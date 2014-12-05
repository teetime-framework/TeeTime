package teetime.framework;

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class Stage {

	public abstract String getId();

	public abstract Stage getParentStage();

	public abstract void setParentStage(Stage parentStage, int index);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	public abstract void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);

	protected abstract void executeWithPorts();

	protected abstract void onSignal(ISignal signal, InputPort<?> inputPort);

	protected abstract TerminationStrategy getTerminationStrategy();

	protected abstract void terminate();

	protected abstract boolean shouldBeTerminated();
}
