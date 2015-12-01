/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.List;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.strategy.IDistributorStrategy;
import teetime.stage.basic.distributor.strategy.NonBlockingRoundRobinStrategy;

/**
 * New output ports can be created by calling {@link #getNewOutputPort()}.
 *
 * @stage.sketch
 *
 * 				<pre>
 *     +----------------------------+
 *     |                            |
 *     |                          +---+
 *     |           +------------&gt; |   |
 *     |           |              +---+
 *     |           |                |
 *   +---+         |
 *   |   | +-------+--- . . .       .
 *   +---+         |
 *     |           |                |
 *     |           |              +---+
 *     |           +------------&gt; |   |
 *     |                          +---+
 *     |                            |
 *     +----------------------------+
 *               </pre>
 *
 * @stage.output The incoming element will be passed to output ports, which are selected by a strategy.
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @since 1.0
 *
 * @param <T>
 *            the type of both the input and output ports
 */
public class Distributor<T> extends AbstractConsumerStage<T> {

	protected IDistributorStrategy strategy;

	public Distributor() {
		this(new NonBlockingRoundRobinStrategy());
	}

	public Distributor(final IDistributorStrategy strategy) {
		this.strategy = strategy;
		addOutputPortRemovedListener(strategy);
	}

	@Override
	protected void execute(final T element) {
		this.strategy.distribute(this.getOutputPorts(), element);
	}

	public OutputPort<T> getNewOutputPort() { // make public
		return this.createOutputPort();
	}

	public IDistributorStrategy getStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IDistributorStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public List<OutputPort<?>> getOutputPorts() { // make public
		return super.getOutputPorts();
	}

}
