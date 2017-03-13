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

import teetime.framework.InputPort;
import teetime.stage.basic.AbstractFilter;

public final class ElementDelayMeasuringStage<T> extends AbstractFilter<T> {

	private final InputPort<Long> triggerInputPort = this.createInputPort();

	private long numPassedElements;
	private long lastTimestampInNs;

	private final List<Long> delays = new LinkedList<Long>();

	@Override
	protected void execute(final T element) {
		Long timestampInNs = this.triggerInputPort.receive();
		if (timestampInNs != null) {
			this.computeElementDelay(System.nanoTime());
		}

		this.numPassedElements++;
		this.outputPort.send(element);
	}

	@Override
	public void onStarting() throws Exception { // NOPMD
		super.onStarting();
		this.resetTimestamp(System.nanoTime());
	}

	private void computeElementDelay(final Long timestampInNs) {
		if (this.numPassedElements > 0) {
			long diffInNs = timestampInNs - this.lastTimestampInNs;
			long delayInNsPerElement = diffInNs / this.numPassedElements;
			this.delays.add(delayInNsPerElement);
			this.logger.info("Delay: " + delayInNsPerElement + " time units/element");

			this.resetTimestamp(timestampInNs);
		}
	}

	private void resetTimestamp(final Long timestampInNs) {
		this.numPassedElements = 0;
		this.lastTimestampInNs = timestampInNs;
	}

	public List<Long> getDelays() {
		return this.delays;
	}

	public InputPort<Long> getTriggerInputPort() {
		return this.triggerInputPort;
	}

}
