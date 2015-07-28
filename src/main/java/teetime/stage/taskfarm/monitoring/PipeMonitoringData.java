package teetime.stage.taskfarm.monitoring;

public class PipeMonitoringData implements IMonitoringData {

	private final long time;
	private final long numPushes;
	private final long numPulls;
	private final int size;
	private final int capacity;
	private final long pushThroughput;
	private final long pullThroughput;
	private final int numWaits;

	PipeMonitoringData(final long time, final long numPushes, final long numPulls, final int size, final int capacity, final long pushThroughput,
			final long pullThroughput,
			final int numWaits) {
		this.time = time;
		this.numPushes = numPushes;
		this.numPulls = numPulls;
		this.size = size;
		this.capacity = capacity;
		this.pushThroughput = pushThroughput;
		this.pullThroughput = pullThroughput;
		this.numWaits = numWaits;
	}

	public long getNumPushes() {
		return numPushes;
	}

	public long getNumPulls() {
		return numPulls;
	}

	public int getSize() {
		return size;
	}

	public int getCapacity() {
		return capacity;
	}

	public long getPushThroughput() {
		return pushThroughput;
	}

	public long getPullThroughput() {
		return pullThroughput;
	}

	public int getNumWaits() {
		return numWaits;
	}

	public long getTime() {
		return time;
	}
}
