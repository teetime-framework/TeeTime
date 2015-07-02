package teetime.stage.taskfarm.monitoring;

public class ThroughputEntry {

	private long timestamp;
	private double throughput;

	public ThroughputEntry() {};

	public ThroughputEntry(final long timestamp, final double throughput) {
		this.timestamp = timestamp;
		this.throughput = throughput;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public double getThroughput() {
		return throughput;
	}

	public void setThroughput(final double throughput) {
		this.throughput = throughput;
	}
}
