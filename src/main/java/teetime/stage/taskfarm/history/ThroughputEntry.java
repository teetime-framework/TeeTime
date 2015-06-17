package teetime.stage.taskfarm.history;

public class ThroughputEntry {

	private long timestamp;
	private int throughput;

	public ThroughputEntry() {};

	public ThroughputEntry(final long timestamp, final int throughput) {
		this.timestamp = timestamp;
		this.throughput = throughput;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(final int throughput) {
		this.throughput = throughput;
	}
}
