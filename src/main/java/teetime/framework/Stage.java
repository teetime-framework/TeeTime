package teetime.framework;

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public interface Stage {

	String getId();

	void executeWithPorts();

	Stage getParentStage();

	void setParentStage(Stage parentStage, int index);

	void onSignal(ISignal signal, InputPort<?> inputPort);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);
}
