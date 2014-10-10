package teetime.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.ConsumerStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class ElementDelayMeasuringStage<T> extends ConsumerStage<T> {

	private final InputPort<Long> triggerInputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> delays = new LinkedList<Long>();

	@Override
	protected void execute(final T element) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementDelay(System.nanoTime());
		}

		this.numPassedElements++;
		this.send(this.outputPort, element);
	}

	@Override
	public void onStarting() {
		this.resetTimestamp(System.nanoTime());
		super.onStarting();
	}

	private void computeElementDelay(final Long timestampInNs) {
		long diffInNs = timestampInNs - this.lastTimestampInNs;
		if (this.numPassedElements > 0) {
			long delayInNsPerElement = diffInNs / this.numPassedElements;
			this.delays.add(delayInNsPerElement);
			this.logger.info("Delay: " + delayInNsPerElement + " time units/element");

			this.resetTimestamp(timestampInNs);
		}
	}

	private void resetTimestamp(final Long timestampInNs) {
		this.numPassedElements = 0;
		this.lastTimestampInNs = timestampInNs;
	}

	public List<Long> getDelays() {
		return this.delays;
	}

	public InputPort<Long> getTriggerInputPort() {
		return this.triggerInputPort;
	}

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
