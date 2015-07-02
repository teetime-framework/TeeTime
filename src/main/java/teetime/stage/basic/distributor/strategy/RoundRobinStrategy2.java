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
package teetime.stage.basic.distributor.strategy;

import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.stage.basic.distributor.Distributor;

/**
 * @author Christian Wulf
 *
 * @since 1.1
 */
public final class RoundRobinStrategy2 implements IDistributorStrategy {

	private int index;
	private int numWaits;

	@Override
	public <T> boolean distribute(final OutputPort<T>[] outputPorts, final T element) {
		final int numOutputPorts = outputPorts.length;
		int numLoops = numOutputPorts;

		boolean success;
		OutputPort<T> outputPort;
		do {
			outputPort = getNextPortInRoundRobinOrder(outputPorts);
			success = outputPort.sendNonBlocking(element);
			if (0 == numLoops) {
				numWaits++;
				backoff();
				numLoops = numOutputPorts;
			}
			numLoops--;
		} while (!success);

		return true;
	}

	private void backoff() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Thread.yield();
	}

	private <T> OutputPort<T> getNextPortInRoundRobinOrder(final OutputPort<T>[] outputPorts) {
		final OutputPort<T> outputPort = outputPorts[this.index];

		this.index = (this.index + 1) % outputPorts.length;

		return outputPort;
	}

	public int getNumWaits() {
		return numWaits;
	}

	@Override
	public void onOutputPortRemoved(final Stage stage, final OutputPort<?> removedOutputPort) {
		Distributor<?> distributor = (Distributor<?>) stage;
		// correct the index if it is out-of-bounds
		this.index = this.index % distributor.getOutputPorts().length;
	}

}
