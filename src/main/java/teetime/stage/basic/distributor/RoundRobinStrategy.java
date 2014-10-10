/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.stage.basic.distributor;

import teetime.framework.OutputPort;

/**
 * @author Nils Christian Ehmke
 * 
 * @since 1.10
 */
public final class RoundRobinStrategy<T> implements IDistributorStrategy<T> {

	private int index = 0;

	@Override
	public boolean distribute(final OutputPort<T>[] outputPorts, final T element) {
		final OutputPort<T> outputPort = this.getNextPortInRoundRobinOrder(outputPorts);

		outputPort.send(element);

		return true;
	}

	private OutputPort<T> getNextPortInRoundRobinOrder(final OutputPort<T>[] outputPorts) {
		final OutputPort<T> outputPort = outputPorts[this.index];

		this.index = (this.index + 1) % outputPorts.length;

		return outputPort;
	}

}
