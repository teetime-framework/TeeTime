/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.adaptation.monitoring;

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
