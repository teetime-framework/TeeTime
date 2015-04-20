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
package teetime.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class AbstractStage extends Stage {

	private static final IPipe DUMMY_PORT = new DummyPipe();

	private final Set<ISignal> triggeredSignals = new HashSet<ISignal>();

	private InputPort<?>[] inputPorts = new InputPort<?>[0];
	private OutputPort<?>[] outputPorts = new OutputPort<?>[0];
	private StageState currentState = StageState.CREATED;

	@Override
	public InputPort<?>[] getInputPorts() {
		return inputPorts;
	}

	@Override
	protected OutputPort<?>[] getOutputPorts() {
		return this.outputPorts;
	}

	@Override
	public StageState getCurrentState() {
		return currentState;
	}

	/**
	 * May not be invoked outside of IPipe implementations
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (!this.signalAlreadyReceived(signal, inputPort)) {
			signal.trigger(this);

			for (OutputPort<?> outputPort : outputPorts) {
				outputPort.sendSignal(signal);
			}
		}
	}

	/**
	 * @param signal
	 *            arriving signal
	 * @param inputPort
	 *            which received the signal
	 * @return <code>true</code> if this stage has already received the given <code>signal</code>, <code>false</code> otherwise
	 */
	protected boolean signalAlreadyReceived(final ISignal signal, final InputPort<?> inputPort) {
		if (this.triggeredSignals.contains(signal)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal: " + signal + " again from input port: " + inputPort);
			}
			return true;
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal: " + signal + " from input port: " + inputPort);
			}
			this.triggeredSignals.add(signal);
			return false;
		}
	}

	@Override
	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		this.validateOutputPorts(invalidPortConnections);
		currentState = StageState.VALIDATED;
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@Override
	public void onStarting() throws Exception {
		this.owningThread = Thread.currentThread();

		this.connectUnconnectedOutputPorts();
		currentState = StageState.STARTED;
		logger.trace("Started.");
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private void connectUnconnectedOutputPorts() {
		for (OutputPort<?> outputPort : this.outputPorts) {
			if (null == outputPort.getPipe()) { // if port is unconnected
				if (logger.isInfoEnabled()) {
					this.logger.info("Unconnected output port: " + outputPort + ". Connecting with a dummy output port.");
				}
				outputPort.setPipe(DUMMY_PORT);
			}
		}
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@Override
	public void onTerminating() throws Exception {
		currentState = StageState.TERMINATED;
		logger.trace("Terminated.");
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @return Newly added InputPort
	 *
	 */
	// * @deprecated Since 1.1. Use {@link #createInputPort(Class)} instead.
	@SuppressWarnings("unchecked")
	// @Deprecated
	protected <T> InputPort<T> createInputPort() {
		return (InputPort<T>) createInputPort(null);
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @param type
	 * @return Newly added InputPort
	 */
	protected <T> InputPort<T> createInputPort(final Class<T> type) {
		final InputPort<T> inputPort = new InputPort<T>(type, this);
		inputPorts = addElementToArray(inputPort, inputPorts);
		return inputPort;
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @return Newly added OutputPort
	 *
	 */
	// * @deprecated Since 1.1. Use {@link #createOutputPort(Class)} instead.
	@SuppressWarnings("unchecked")
	// @Deprecated
	protected <T> OutputPort<T> createOutputPort() {
		return (OutputPort<T>) createOutputPort(null);
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @param type
	 * @return Newly added OutputPort
	 */
	protected <T> OutputPort<T> createOutputPort(final Class<T> type) {
		final OutputPort<T> outputPort = new OutputPort<T>(type);
		outputPorts = addElementToArray(outputPort, outputPorts);
		return outputPort;
	}

	private <T> T[] addElementToArray(final T element, final T[] srcArray) {
		T[] newOutputPorts = Arrays.copyOf(srcArray, srcArray.length + 1);
		newOutputPorts[srcArray.length] = element;
		return newOutputPorts;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (OutputPort<?> outputPort : outputPorts) {
			final IPipe pipe = outputPort.getPipe();
			if (null != pipe) { // if output port is connected with another one
				final Class<?> sourcePortType = outputPort.getType();
				final Class<?> targetPortType = pipe.getTargetPort().getType();
				if (null == sourcePortType || !sourcePortType.equals(targetPortType)) {
					final InvalidPortConnection invalidPortConnection = new InvalidPortConnection(outputPort, pipe.getTargetPort());
					invalidPortConnections.add(invalidPortConnection);
				}
			}
		}
	}

	@Override
	protected void terminate() {
		currentState = StageState.TERMINATING;
	}

	@Override
	protected boolean shouldBeTerminated() {
		return (currentState == StageState.TERMINATING);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return TerminationStrategy.BY_SIGNAL;
	}

}
