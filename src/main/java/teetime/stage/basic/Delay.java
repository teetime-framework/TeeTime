package teetime.stage.basic;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class Delay<T> extends AbstractStage {

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
			returnNoElement();
		}

		sendAllBufferedEllements();
	}

	private void sendAllBufferedEllements() {
		while (!bufferedElements.isEmpty()) {
			T element = bufferedElements.remove(0);
			outputPort.send(element);
			logger.trace("Sent buffered element: " + element);
		}
	}

	@Override
	public void onTerminating() throws Exception {
		while (null == timestampTriggerInputPort.receive()) {
			// wait for the next trigger
		}

		sendAllBufferedEllements();

		T element;
		while (null != (element = inputPort.receive())) {
			outputPort.send(element);
			logger.trace("Sent element: " + element);
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
