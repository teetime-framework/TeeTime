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
package teetime.stage.basic.merger;

import teetime.framework.InputPort;

/**
 * @author Nils Christian Ehmke
 * 
 * @since 1.10
 */
public final class RoundRobinStrategy<T> implements IMergerStrategy<T> {

	private int index = 0;

	@Override
	public T getNextInput(final Merger<T> merger) {
		@SuppressWarnings("unchecked")
		InputPort<T>[] inputPorts = (InputPort<T>[]) merger.getInputPorts();
		int size = inputPorts.length;
		// check each port at most once to avoid a potentially infinite loop
		while (size-- > 0) {
			InputPort<T> inputPort = this.getNextPortInRoundRobinOrder(inputPorts);
			final T token = inputPort.receive();
			if (token != null) {
				return token;
			}
		}
		return null;
	}

	private InputPort<T> getNextPortInRoundRobinOrder(final InputPort<T>[] inputPorts) {
		InputPort<T> inputPort = inputPorts[this.index];

		this.index = (this.index + 1) % inputPorts.length;

		return inputPort;
	}

}
