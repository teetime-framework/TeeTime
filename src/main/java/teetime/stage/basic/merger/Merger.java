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
package teetime.stage.basic.merger;

import java.util.List;

import teetime.framework.*;
import teetime.stage.basic.merger.strategy.IMergerStrategy;
import teetime.stage.basic.merger.strategy.RoundRobinStrategy;

/**
 *
 * This stage merges data from the input ports, by taking elements according to the chosen merge strategy and by putting them to the output port.
 * New input ports can be created by calling {@link #getNewInputPort()}.
 *
 * @stage.sketch
 *
 *               <pre>
 *   +---------------------------+
 *   |                           |
 * +---+                         |
 * |   | +-----------+           |
 * +---+             |           |
 *   |               |           |
 *                   |         +---+
 *   .      . . . ---+-------&gt; |   |
 *                   |         +---+
 *   |               |           |
 * +---+             |           |
 * |   | +-----------+           |
 * +---+                         |
 *   |                           |
 *   +---------------------------+
 *
 *
 *               </pre>
 *
 * @author Christian Wulf
 *
 * @since 1.0
 *
 * @param T
 *            the type of the input port and the output ports
 */
public class Merger<T> extends AbstractStage {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private IMergerStrategy strategy;

	/**
	 * A merger using the {@link RoundRobinStrategy}}.
	 */
	public Merger() {
		this(new RoundRobinStrategy());
	}

	public Merger(final IMergerStrategy strategy) {
		this.strategy = strategy;
		addInputPortRemovedListener(strategy);
	}

	@Override
	protected void execute() {
		final T token = this.strategy.getNextInput(this);
		if (token == null) {
			return;
		}
		outputPort.send(token);
	}

	public void setStrategy(final IMergerStrategy strategy) {
		this.strategy = strategy;
	}

	public IMergerStrategy getStrategy() {
		return this.strategy;
	}

	@Override
	public List<InputPort<?>> getInputPorts() { // make public
		return super.getInputPorts();
	}

	public InputPort<T> getNewInputPort() {
		return this.createInputPort();
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
