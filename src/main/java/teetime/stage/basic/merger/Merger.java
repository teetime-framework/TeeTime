/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.stage.basic.merger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.signal.ISignal;

/**
 *
 * This stage merges data from the input ports, by taking elements according to the chosen merge strategy and by putting them to the output port.
 * For its signal handling behavior see {@link #onSignal(ISignal, InputPort)}
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @since 1.0
 *
 * @param <T>
 *            the type of both the input and output ports
 */
public final class Merger<T> extends AbstractStage {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private IMergerStrategy strategy;

	private final Map<Class<ISignal>, Set<InputPort<?>>> signalMap = new HashMap<Class<ISignal>, Set<InputPort<?>>>();

	public Merger() {
		this(new RoundRobinStrategy());
	}

	public Merger(final IMergerStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void executeStage() {
		final T token = this.strategy.getNextInput(this);
		if (token == null) {
			returnNoElement();
		}
		outputPort.send(token);
	}

	/**
	 * This method is executed, if a signal is sent to a instance of this class.
	 * Multiple signals of one certain type are ignored, if they are sent to same port.
	 * Hence a signal is only passed on, when it arrived on all input ports, regardless how often.
	 *
	 * @param signal
	 *            Signal which is sent
	 *
	 * @param inputPort
	 *            The port which the signal was sent to
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (logger.isTraceEnabled()) {
			this.logger.trace("Got signal: " + signal + " from input port: " + inputPort);
		}

		Class<? extends ISignal> signalClass = signal.getClass();

		Set<InputPort<?>> inputPorts;
		if (signalMap.containsKey(signalClass)) {
			inputPorts = signalMap.get(signalClass);
		} else {
			inputPorts = new HashSet<InputPort<?>>();
			signalMap.put((Class<ISignal>) signalClass, inputPorts);
		}

		if (!inputPorts.add(inputPort)) {
			this.logger.warn("Received more than one signal - " + signal + " - from input port: " + inputPort);
		}

		if (signal.mayBeTriggered(inputPorts, getInputPorts())) {
			super.onSignal(signal, inputPort);
		}

		// if (signalMap.containsKey(signalClass)) {
		// Set<InputPort<?>> set = signalMap.get(signalClass);
		// if (!set.add(inputPort)) {
		// this.logger.warn("Received more than one signal - " + signal + " - from input port: " + inputPort);
		// }
		// if (signalMap.get(signalClass).size() == this.getInputPorts().length && signalClass == TerminatingSignal.class) {
		// triggerAndPassOn(signal);
		// // signalMap.remove(signalClass);
		// }
		// } else {
		// Set<InputPort<?>> tempSet = new HashSet<InputPort<?>>();
		// signalMap.put((Class<ISignal>) signalClass, tempSet);
		// tempSet.add(inputPort);
		// if (signalClass == InitializingSignal.class || signalClass == StartingSignal.class) {
		// triggerAndPassOn(signal);
		// }
		// }

	}

	// private void triggerAndPassOn(final ISignal signal) {
	// signal.trigger(this);
	// sendSignalToOutputPorts(signal);
	// }

	// private void sendSignalToOutputPorts(final ISignal signal) {
	// for (OutputPort<?> outputPort : getOutputPorts()) {
	// outputPort.sendSignal(signal);
	// }
	// }

	public IMergerStrategy getMergerStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IMergerStrategy strategy) {
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
