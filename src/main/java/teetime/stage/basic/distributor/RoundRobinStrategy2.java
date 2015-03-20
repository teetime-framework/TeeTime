/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.stage.basic.distributor;

import teetime.framework.OutputPort;

/**
 * @author Christian Wulf
 *
 * @since 1.1
 */
public final class RoundRobinStrategy2 implements IDistributorStrategy {

	private int index = 0;
	private int numWaits;

	@Override
	public <T> boolean distribute(final OutputPort<T>[] outputPorts, final T element) {
		final int numOutputPorts = outputPorts.length;
		int numLoops = numOutputPorts;

		boolean success;
		do {
			final OutputPort<T> outputPort = getNextPortInRoundRobinOrder(outputPorts);
			success = outputPort.sendNonBlocking(element);
			if (0 == numLoops) {
				numWaits++;
				// Thread.yield();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				numLoops = numOutputPorts;
			}
			numLoops--;
		} while (!success);

		return true;
	}

	private <T> OutputPort<T> getNextPortInRoundRobinOrder(final OutputPort<T>[] outputPorts) {
		final OutputPort<T> outputPort = outputPorts[this.index];

		this.index = (this.index + 1) % outputPorts.length;

		return outputPort;
	}

	public int getNumWaits() {
		return numWaits;
	}

}
