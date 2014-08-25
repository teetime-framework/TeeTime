package teetime.variant.methodcallWithPorts.framework.core;

import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;
import teetime.variant.methodcallWithPorts.framework.core.validation.InvalidPortConnection;

public interface StageWithPort {

	String getId();

	void executeWithPorts();

	StageWithPort getParentStage();

	void setParentStage(StageWithPort parentStage, int index);

	// void setListener(OnDisableListener listener);

	/**
	 * @return <code>true</code> iff this stage makes progress when it is re-executed by the scheduler, otherwise <code>false</code>.<br>
	 *         For example, many stages are re-schedulable if at least one of their input ports are not empty.
	 */
	boolean isReschedulable();

	void onIsPipelineHead();

	void onSignal(Signal signal, InputPort<?> inputPort);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);
}
