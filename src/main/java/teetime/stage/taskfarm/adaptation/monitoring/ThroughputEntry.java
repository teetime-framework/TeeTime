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
