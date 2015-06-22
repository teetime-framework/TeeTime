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

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		OutputPort<?> outputPortToRemove;

		if (dynamicDistributor instanceof ControlledDynamicDistributor) {
			// for testing purposes only
			OutputPort<?>[] outputPorts = ((ControlledDynamicDistributor<?>) dynamicDistributor).getOutputPorts();
			outputPortToRemove = outputPorts[outputPorts.length - 1];
		} else {
			outputPortToRemove = outputPort;
		}

		outputPortToRemove.sendSignal(new TerminatingSignal());

		dynamicDistributor.removeDynamicPort((DynamicOutputPort<?>) outputPortToRemove);
	}
}
