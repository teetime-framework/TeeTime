package teetime.stage.basic;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class Delay<T> extends AbstractStage {

	private final InputPort<T> inputPort = this.createInputPort();
	private final InputPort<Long> timestampTriggerInputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	private final List<T> bufferedElements = new LinkedList<T>();

	@Override
	public void executeWithPorts() {
		T element = inputPort.receive();
		if (null != element) {
			bufferedElements.add(element);
		}

		Long timestampTrigger = this.timestampTriggerInputPort.receive();
		if (null == timestampTrigger) {
			return;
		}

		while (!bufferedElements.isEmpty()) {
			element = bufferedElements.remove(0);
			this.send(this.outputPort, element);
		}
	}

	@Override
	public void onTerminating() throws Exception {
		while (!this.inputPort.getPipe().isEmpty()) {
			this.executeWithPorts();
		}
		super.onTerminating();
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
