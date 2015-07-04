package teetime.stage.taskfarm.monitoring;

import java.util.LinkedList;
import java.util.List;

/**
 * The ThroughputHistory contains a relevant number of {@link ThroughputEntry}s.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class ThroughputHistory {

	private static final int MAX_ENTRIES = 20;

	private final LinkedList<ThroughputEntry> entries = new LinkedList<ThroughputEntry>();

	public ThroughputHistory() {}

	public List<ThroughputEntry> getEntries() {
		return this.entries;
	}

	/**
	 * Adds a throughput to the beginning of the history. Adds current timestamp in millis.
	 *
	 * @param throughput
	 *            new measured throughput value
	 */
	public void add(final double throughput) {
		final long timestamp = System.currentTimeMillis();
		final ThroughputEntry entry = new ThroughputEntry(timestamp, throughput);
		this.addEntry(entry);
	}

	private void addEntry(final ThroughputEntry entry) {
		this.entries.addFirst(entry);
		if (this.entries.size() > MAX_ENTRIES) {
			this.entries.removeLast();
		}
	}

	/**
	 * Gets the throughput value in a given history position.
	 *
	 * @param entry
	 *            history index
	 * @return throughput value
	 */
	public double getThroughputOfEntry(final int entry) {
		return this.entries.get(entry).getThroughput();
	}

	/**
	 * Gets the timestamp value in a given history position.
	 *
	 * @param entry
	 *            history index
	 * @return timestamp value
	 */
	public long getTimestampOfEntry(final int entry) {
		return this.entries.get(entry).getTimestamp();
	}
}
