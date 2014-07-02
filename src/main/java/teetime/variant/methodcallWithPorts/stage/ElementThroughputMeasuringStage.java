package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public class ElementThroughputMeasuringStage<T> extends ConsumerStage<T, T> {

	private final InputPort<Long> triggerInputPort = new InputPort<Long>(this);

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> throughputs = new LinkedList<Long>();

	@Override
	protected void execute5(final T element) {
		this.numPassedElements++;
		this.send(element);

		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementThroughput(System.nanoTime());
		}
	}

	@Override
	public void onStart() {
		this.resetTimestamp(System.nanoTime());
		super.onStart();
	}

	private void computeElementThroughput(final Long timestampInNs) {
		long diffInNs = timestampInNs - this.lastTimestampInNs;
		long diffInSec = TimeUnit.NANOSECONDS.toSeconds(diffInNs);
		if (diffInSec > 0) {
			long throughputPerSec = this.numPassedElements / diffInSec;
			this.throughputs.add(throughputPerSec);
			this.logger.info("Throughput: " + throughputPerSec + " elements/s" + " -> numPassedElements=" + this.numPassedElements);

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
