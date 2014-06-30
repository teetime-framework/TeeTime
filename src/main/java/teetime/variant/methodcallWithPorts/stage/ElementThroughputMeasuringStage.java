package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public class ElementThroughputMeasuringStage<T> extends ConsumerStage<T, T> {

	private final InputPort<Long> triggerInputPort = new InputPort<Long>(this);

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> throughputs = new LinkedList<Long>();

	@Override
	protected void execute5(final T element) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementThroughput(System.nanoTime());
		}
		this.numPassedElements++;
		this.send(element);
	}

	@Override
	public void onStart() {
		this.resetTimestamp(System.nanoTime());
		super.onStart();
	}

	private void computeElementThroughput(final Long timestampInNs) {
		long diffInNs = timestampInNs - this.lastTimestampInNs;
		if (diffInNs > 0) {
			long throughputInNsPerElement = this.numPassedElements / diffInNs;
			this.throughputs.add(throughputInNsPerElement);
			this.logger.info("Throughput: " + throughputInNsPerElement + " elements/time unit");

			this.resetTimestamp(timestampInNs);
		}
	}

	private void resetTimestamp(final Long timestampInNs) {
		this.numPassedElements = 0;
		this.lastTimestampInNs = timestampInNs;
	}

	public List<Long> getThroughputs() {
		return this.throughputs;
	}

	public InputPort<Long> getTriggerInputPort() {
		return this.triggerInputPort;
	}

}
