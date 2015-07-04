package teetime.stage.taskfarm.monitoring;

/**
 * A ThroughputEntry is a measured throughput value with its timestamp.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class ThroughputEntry {

	private long timestamp;
	private double throughput;

	/**
	 * Constructor.
	 *
	 * @param timestamp
	 *            timestamp in millis
	 * @param throughput
	 *            measured throughput value
	 */
	public ThroughputEntry(final long timestamp, final double throughput) {
		this.timestamp = timestamp;
		this.throughput = throughput;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public double getThroughput() {
		return this.throughput;
	}

	public void setThroughput(final double throughput) {
		this.throughput = throughput;
	}
}
