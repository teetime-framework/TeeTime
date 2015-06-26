package teetime.stage.basic.merger.dynamic;

import teetime.framework.DynamicInputPort;
import teetime.framework.InputPort;
import teetime.util.framework.port.PortAction;

public class RemovePortAction<T> implements PortAction<DynamicMerger<T>> {

	private final DynamicInputPort<T> inputPort;

	public RemovePortAction(final DynamicInputPort<T> inputPort) {
		super();
		this.inputPort = inputPort;
	}

	@Override
	public void execute(final DynamicMerger<T> dynamicMerger) {
		InputPort<?> inputPortsToRemove;

		// if (dynamicMerger instanceof ControlledDynamicMerger) {
		// // for testing purposes only
		// InputPort<?>[] inputPorts = ((ControlledDynamicMerger<?>) dynamicMerger).getInputPorts();
		// inputPortsToRemove = inputPorts[inputPorts.length - 1];
		// } else {
		inputPortsToRemove = inputPort;
		// }

		dynamicMerger.removeDynamicPort((DynamicInputPort<?>) inputPortsToRemove);
	}
}
