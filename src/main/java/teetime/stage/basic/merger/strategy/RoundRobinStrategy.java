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
package teetime.stage.basic.merger.strategy;

import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.stage.basic.merger.Merger;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public final class RoundRobinStrategy implements IMergerStrategy {

	private int index = 0;

	@Override
	public <T> T getNextInput(final Merger<T> merger) {
		final InputPort<T>[] inputPorts = merger.getInputPorts();
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

	private <T> InputPort<T> getNextPortInRoundRobinOrder(final InputPort<T>[] inputPorts) {
		InputPort<T> inputPort = inputPorts[this.index];

		this.index = (this.index + 1) % inputPorts.length;

		return inputPort;
	}

	@Override
	public void onInputPortRemoved(final Stage stage, final InputPort<?> removedInputPort) {
		Merger<?> merger = (Merger<?>) stage;
		// correct the index if it is out-of-bounds
		this.index = (this.index + 1) % merger.getInputPorts().length;
	}

}
