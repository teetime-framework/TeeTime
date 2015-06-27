package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicOutputPort;
import teetime.framework.OutputPort;
import teetime.util.framework.port.PortAction;

public class RemovePortAction<T> implements PortAction<DynamicDistributor<T>> {

	private final DynamicOutputPort<T> outputPort;

	public RemovePortAction(final DynamicOutputPort<T> outputPort) {
		super();
		this.outputPort = outputPort;
	}

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		OutputPort<?> outputPortToRemove;

		if (null == outputPort) {
			// for testing purposes only
			OutputPort<?>[] outputPorts = ((DynamicDistributor<?>) dynamicDistributor).getOutputPorts();
			outputPortToRemove = outputPorts[outputPorts.length - 1];
		} else {
			outputPortToRemove = outputPort;
		}

		dynamicDistributor.removeDynamicPort((DynamicOutputPort<?>) outputPortToRemove);
	}
}
