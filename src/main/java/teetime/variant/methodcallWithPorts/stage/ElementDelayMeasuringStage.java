package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public class ElementDelayMeasuringStage<T> extends ConsumerStage<T, T> {

	private final InputPort<Long> triggerInputPort = new InputPort<Long>(this);

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> delays = new LinkedList<Long>();

	@Override
	protected void execute5(final T element) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementDelay(System.nanoTime());
		}
		this.numPassedElements++;
		this.send(element);
	}

	@Override
	public void onStart() {
		this.resetTimestamp(System.nanoTime());
		super.onStart();
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

}
