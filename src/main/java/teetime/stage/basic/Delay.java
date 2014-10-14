package teetime.stage.basic;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

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
	public void onTerminating() {
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
