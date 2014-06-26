package teetime.variant.methodcallWithPorts.stage;

import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;

public class ThroughputFilter<T> extends ConsumerStage<T, T> {

	private final InputPort<Long> triggerInputPort = new InputPort<Long>(this);

	private long numPassedElements;
	private long timestamp;

	private final List<Long> throughputs = new LinkedList<Long>();

	@Override
	protected void execute5(final T element) {
		Long trigger = this.triggerInputPort.receive();
		if (trigger != null) {
			this.computeThroughput();
			this.resetTimestamp();
		}
		this.numPassedElements++;
		this.send(element);
	}

	@Override
	public void onStart() {
		this.resetTimestamp();
		super.onStart();
	}

	private void computeThroughput() {
		long diffInNs = System.nanoTime() - this.timestamp;
		long throughput = this.numPassedElements / diffInNs;
		// this.throughputs.add(throughput);
		this.logger.info("Throughput: " + throughput + " ns");
	}

	private void resetTimestamp() {
		this.numPassedElements = 0;
		this.timestamp = System.nanoTime();
	}

	public List<Long> getThroughputs() {
		return this.throughputs;
	}

	public InputPort<Long> getTriggerInputPort() {
		return triggerInputPort;
	}

}
