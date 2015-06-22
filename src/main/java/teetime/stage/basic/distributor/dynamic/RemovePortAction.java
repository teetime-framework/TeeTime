package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicOutputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.TerminatingSignal;

public class RemovePortAction<T> implements PortAction<T> {

	private final DynamicOutputPort<T> outputPort;

	public RemovePortAction(final DynamicOutputPort<T> outputPort) {
		super();
		this.outputPort = outputPort;
	}

	// public DynamicOutputPort<T> getOutputPort() {
	// return outputPort;
	// }

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		System.out.println("Removing...");
		if (dynamicDistributor instanceof ControlledDynamicDistributor) {
			OutputPort<?>[] outputPorts = ((ControlledDynamicDistributor<?>) dynamicDistributor).getOutputPorts();
			OutputPort<?> outputPortToRemove = outputPorts[outputPorts.length - 1];
			// outputPortToRemove = outputPort;

			outputPortToRemove.sendSignal(new TerminatingSignal());

			dynamicDistributor.removeDynamicPort((DynamicOutputPort<?>) outputPortToRemove);
		}
		System.out.println("Removed.");
	}
}
