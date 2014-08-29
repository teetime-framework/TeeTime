package teetime.variant.methodcallWithPorts.stage.basic;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

public class Delay<T> extends AbstractStage {

	private final InputPort<T> inputPort = this.createInputPort();
	private final InputPort<Long> timestampTriggerInputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	@Override
	public void executeWithPorts() {
		Long timestampTrigger = this.timestampTriggerInputPort.receive();
		if (null == timestampTrigger) {
			return;
		}
		// System.out.println("got timestamp; #elements: " + this.getInputPort().pipe.size());

		// System.out.println("#elements: " + this.getInputPort().pipe.size());
		// TODO implement receiveAll() and sendMultiple()
		while (!this.inputPort.getPipe().isEmpty()) {
			T element = this.inputPort.receive();
			this.send(this.outputPort, element);
		}
	}

	@Override
	public void onIsPipelineHead() {
		while (!this.inputPort.getPipe().isEmpty()) {
			this.executeWithPorts();
		}
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}

	public InputPort<Long> getTimestampTriggerInputPort() {
		return this.timestampTriggerInputPort;
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
