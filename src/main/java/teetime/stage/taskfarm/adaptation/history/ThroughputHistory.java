/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.adaptation.history;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.taskfarm.TaskFarmConfiguration;

/**
 * Represents a container of multiple {@link ThroughputEntry}.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class ThroughputHistory {

	/** analysis window **/
	private final int maxEntries;

	/** throughput sums **/
	private final LinkedList<ThroughputEntry> entries = new LinkedList<>(); // NOPMD LinkedList is needed as type in the code

	/**
	 * Creates a new throughput history with the analysis window specified in the configuration.
	 *
	 * @param configuration
	 *            configuration of corresponding task farm
	 */
	public ThroughputHistory(final TaskFarmConfiguration<?, ?, ?> configuration) {
		this.maxEntries = configuration.getAnalysisWindow() + 1;
	}

	/**
	 * Creates a new throughput history with the analysis window of 20 (for testing purposes)
	 */
	public ThroughputHistory() {
		this.maxEntries = 20;
	}

	/**
	 * @return all measurements of the throughput history.
	 */
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
		if (this.entries.size() > this.maxEntries) {
			this.entries.removeLast();
		}
	}

	/**
	 * Gets the throughput value at a given history position.
	 *
	 * @param entry
	 *            history index
	 * @return throughput value
	 */
	public double getThroughputOfEntry(final int entry) {
		return this.entries.get(entry).getThroughput();
	}

	/**
	 * Gets the timestamp value at a given history position.
	 *
	 * @param entry
	 *            history index
	 * @return timestamp value
	 */
	public long getTimestampOfEntry(final int entry) {
		return this.entries.get(entry).getTimestamp();
	}
}
