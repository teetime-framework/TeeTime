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
package teetime.stage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import teetime.framework.InputPort;
import teetime.stage.basic.AbstractFilter;

public final class ElementThroughputMeasuringStage<T> extends AbstractFilter<T> {

	private final InputPort<Long> triggerInputPort = this.createInputPort();

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> throughputs = new LinkedList<>();

	@Override
	protected void execute(final T element) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementThroughput(System.nanoTime());
		}
		this.numPassedElements++;

		this.outputPort.send(element);
	}

	@Override
	public void onStarting() {
		super.onStarting();
		this.resetTimestamp(System.nanoTime());
	}

	private void computeElementThroughput(final Long timestampInNs) {
		long diffInNs = timestampInNs - this.lastTimestampInNs;
		// the minimum time granularity of the clock is ms
		long diffInMs = TimeUnit.NANOSECONDS.toMillis(diffInNs);
		double throughputPerMs = (double) this.numPassedElements / diffInMs;
		this.logger.info("Throughput: " + String.format("%.3f", throughputPerMs) + " elements/ms" + " -> numPassedElements=" + this.numPassedElements);

		// long throughputPerTimeUnit = -1;
		//
		// long diffInSec = TimeUnit.NANOSECONDS.toSeconds(diffInNs);
		// if (diffInSec > 0) {
		// throughputPerTimeUnit = this.numPassedElements / diffInSec;
		// this.logger.info("Throughput: " + throughputPerTimeUnit + " elements/s" + " -> numPassedElements=" + this.numPassedElements);
		// } else {
		// long diffInMs = TimeUnit.NANOSECONDS.toMillis(diffInNs);
		// if (diffInMs > 0) {
		// throughputPerTimeUnit = this.numPassedElements / diffInMs;
		// this.logger.info("Throughput: " + throughputPerTimeUnit + " elements/ms" + " -> numPassedElements=" + this.numPassedElements);
		//
		// }
		// }

		this.throughputs.add((long) throughputPerMs);
		this.resetTimestamp(timestampInNs);
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
