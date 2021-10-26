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
package teetime.stage.basic.distributor.strategy;

import java.util.List;

import teetime.framework.OutputPort;
import teetime.framework.StateStatisticsUtils;
import teetime.stage.basic.distributor.Distributor;

/**
 * Backoff strategy
 *
 * @author Christian Wulf
 *
 * @since 1.1
 */
public class NonBlockingRoundRobinStrategy implements IDistributorStrategy {

	private int index;
	private int numWaits;

	@SuppressWarnings("unchecked")
	@Override
	public <T> OutputPort<T> distribute(final List<OutputPort<?>> outputPorts, final T element) {
		final int numOutputPorts = outputPorts.size();
		int numLoops = numOutputPorts;

		boolean success;
		OutputPort<T> outputPort;
		do {
			outputPort = (OutputPort<T>) getNextPortInRoundRobinOrder(outputPorts);
			success = outputPort.sendNonBlocking(element);
			if (0 == numLoops) {
				StateStatisticsUtils.sendingFailed(outputPort.getOwningStage());
				numWaits++;
				backoff();
				numLoops = numOutputPorts;
			}
			numLoops--;
		} while (!success);

		StateStatisticsUtils.sendingSucceeded(outputPort.getOwningStage());

		return outputPort;
	}

	private void backoff() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		// Thread.yield();
	}

	private OutputPort<?> getNextPortInRoundRobinOrder(final List<OutputPort<?>> outputPorts) {
		final OutputPort<?> outputPort = outputPorts.get(this.index);

		this.index = (this.index + 1) % outputPorts.size();

		return outputPort;
	}

	public int getNumWaits() {
		return numWaits;
	}

	@Override
	public void onPortRemoved(final OutputPort<?> removedOutputPort) {
		Distributor<?> distributor = (Distributor<?>) removedOutputPort.getOwningStage();
		// correct the index if it is out-of-bounds
		List<OutputPort<?>> outputPorts = distributor.getOutputPorts();
		this.index = this.index % outputPorts.size();
	}

}
