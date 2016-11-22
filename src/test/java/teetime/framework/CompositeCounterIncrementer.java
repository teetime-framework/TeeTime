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
package teetime.framework;

class CompositeCounterIncrementer extends CompositeStage {

	private final InputPort<CounterContainer> inputPort;
	private final OutputPort<CounterContainer> outputPort;

	public CompositeCounterIncrementer(final int depth) {
		if (depth <= 0) { // one counter incrementer is always created
			throw new IllegalArgumentException();
		}

		CounterIncrementer incrementer = new CounterIncrementer();
		this.inputPort = incrementer.getInputPort();

		InputPort<CounterContainer> lastStageInputPort;
		if (depth > 1) {
			CompositeCounterIncrementer lastStage = new CompositeCounterIncrementer(depth - 1);
			lastStageInputPort = lastStage.getInputPort();
			outputPort = lastStage.getOutputPort();
			connectPorts(incrementer.getOutputPort(), lastStageInputPort);
		} else {
			CounterIncrementer lastStage = incrementer;
			lastStageInputPort = lastStage.getInputPort();
			outputPort = lastStage.getOutputPort();
		}

	}

	public InputPort<CounterContainer> getInputPort() {
		return inputPort;
	}

	public OutputPort<CounterContainer> getOutputPort() {
		return outputPort;
	}

}
