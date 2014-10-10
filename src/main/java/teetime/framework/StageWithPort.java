package teetime.framework;

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public interface StageWithPort {

	String getId();

	void executeWithPorts();

	StageWithPort getParentStage();

	void setParentStage(StageWithPort parentStage, int index);

	// BETTER remove this method since it will be replaced by onTerminating()
	void onIsPipelineHead();

	void onSignal(ISignal signal, InputPort<?> inputPort);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);
}
