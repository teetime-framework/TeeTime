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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

/**
 *
 * This stage merges data from the input ports, by taking elements according to the chosen merge strategy and by putting them to the output port.
 *
 * @author Christian Wulf
 *
 * @since 1.0
 *
 * @param <T>
 *            the type of both the input and output ports
 */
public class Merger<T> extends AbstractStage {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private IMergerStrategy<T> strategy = new RoundRobinStrategy<T>();

	private final Map<Class<?>, HashSet<InputPort<?>>> signalMap = new HashMap<Class<?>, HashSet<InputPort<?>>>();

	@Override
	public void executeWithPorts() {
		final T token = this.strategy.getNextInput(this);
		if (token == null) {
			return;
		}

		this.send(this.outputPort, token);
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.logger.trace("Got signal: " + signal + " from input port: " + inputPort);

		if (signalMap.containsKey(signal.getClass())) {
			HashSet<InputPort<?>> set = signalMap.get(signal.getClass());
			if (!set.add(inputPort)) {
				this.logger.trace("Got second signal " + signal + " from input port: " + inputPort);
			}

			if (set.size() == this.getInputPorts().length) {
				this.outputPort.sendSignal(signal);
				signalMap.remove(signal.getClass());
			}
		} else {
			signal.trigger(this);
			HashSet<InputPort<?>> tempSet = new HashSet<InputPort<?>>();
			tempSet.add(inputPort);
			signalMap.put(signal.getClass(), tempSet);
		}

	}

	public IMergerStrategy<T> getMergerStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IMergerStrategy<T> strategy) {
		this.strategy = strategy;
	}

	@Override
	public InputPort<?>[] getInputPorts() {
		return super.getInputPorts();
	}

	public InputPort<T> getNewInputPort() {
		return this.createInputPort();
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}

}
