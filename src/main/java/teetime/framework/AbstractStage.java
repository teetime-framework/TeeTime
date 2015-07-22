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
package teetime.framework;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.util.framework.port.PortList;
import teetime.util.framework.port.PortRemovedListener;

public abstract class AbstractStage extends Stage {

	private static final IPipe DUMMY_PIPE = new DummyPipe();

	private final Set<Class<? extends ISignal>> triggeredSignalTypes = new HashSet<Class<? extends ISignal>>();

	private final PortList<InputPort<?>> inputPorts = new PortList<InputPort<?>>();
	private final PortList<OutputPort<?>> outputPorts = new PortList<OutputPort<?>>();
	private volatile StageState currentState = StageState.CREATED;

	@Override
	protected List<InputPort<?>> getInputPorts() {
		return inputPorts.getOpenedPorts();
	}

	@Override
	protected List<OutputPort<?>> getOutputPorts() {
		return outputPorts.getOpenedPorts();
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
			for (OutputPort<?> outputPort : outputPorts.getOpenedPorts()) {
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
		boolean signalAlreadyReceived = this.triggeredSignalTypes.contains(signal.getClass());
		if (signalAlreadyReceived) {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal again: " + signal + " from input port: " + inputPort);
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal: " + signal + " from input port: " + inputPort);
			}
			this.triggeredSignalTypes.add(signal.getClass());
		}
		return signalAlreadyReceived;
	}

	@Override
	public void onInitializing() throws Exception {
		this.connectUnconnectedOutputPorts();
		changeState(StageState.INITIALIZED);
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private void connectUnconnectedOutputPorts() {
		for (OutputPort<?> outputPort : this.outputPorts.getOpenedPorts()) {
			if (null == outputPort.getPipe()) { // if port is unconnected
				if (logger.isInfoEnabled()) {
					this.logger.info("Unconnected output port: " + outputPort + ". Connecting with a dummy output port.");
				}
				outputPort.setPipe(DUMMY_PIPE);
			}
		}
	}

	private void changeState(final StageState newState) {
		currentState = newState;
		logger.trace(newState.toString());
	}

	@Override
	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		this.validateOutputPorts(invalidPortConnections);
		changeState(StageState.VALIDATED);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@Override
	public void onStarting() throws Exception {
		changeState(StageState.STARTED);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@Override
	public void onTerminating() throws Exception {
		changeState(StageState.TERMINATED);
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @param <T>
	 *            the type of elements to be received
	 *
	 * @return the newly added InputPort
	 *
	 */
	protected <T> InputPort<T> createInputPort() {
		return createInputPort(null, null);
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @param type
	 *            class of elements to be received
	 *
	 * @param <T>
	 *            the type of elements to be received
	 *
	 * @return the newly added InputPort
	 */
	protected <T> InputPort<T> createInputPort(final Class<T> type) {
		return createInputPort(type, null);
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @param name
	 *            a specific name for the new port
	 * @param <T>
	 *            the type of elements to be received
	 *
	 * @return the newly added InputPort
	 *
	 */
	protected <T> InputPort<T> createInputPort(final String name) {
		return createInputPort(null, name);
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @param type
	 *            class of elements to be received
	 * @param name
	 *            a specific name for the new port
	 * @param <T>
	 *            the type of elements to be received
	 *
	 * @return the newly added InputPort
	 */
	protected <T> InputPort<T> createInputPort(final Class<T> type, final String name) {
		final InputPort<T> inputPort = new InputPort<T>(type, this, name);
		inputPorts.add(inputPort);
		return inputPort;
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @param <T>
	 *            the type of elements to be sent
	 *
	 * @return the newly added OutputPort
	 *
	 */
	protected <T> OutputPort<T> createOutputPort() {
		return createOutputPort(null, null);
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @param type
	 *            class of elements to be sent
	 *
	 * @param <T>
	 *            the type of elements to be sent
	 *
	 * @return the newly added OutputPort
	 */
	protected <T> OutputPort<T> createOutputPort(final Class<T> type) {
		return createOutputPort(type, null);
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @param name
	 *            a specific name for the new port
	 *
	 * @param <T>
	 *            the type of elements to be sent
	 *
	 * @return the newly added OutputPort
	 *
	 */
	protected <T> OutputPort<T> createOutputPort(final String name) {
		return createOutputPort(null, name);
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @param name
	 *            a specific name for the new port
	 * @param type
	 *            class of elements to be sent
	 *
	 * @param <T>
	 *            the type of elements to be sent
	 *
	 * @return the newly added OutputPort
	 */
	protected <T> OutputPort<T> createOutputPort(final Class<T> type, final String name) {
		final OutputPort<T> outputPort = new OutputPort<T>(type, this, name);
		outputPorts.add(outputPort);
		return outputPort;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (OutputPort<?> outputPort : outputPorts.getOpenedPorts()) {
			final IPipe pipe = outputPort.getPipe();

			final Class<?> sourcePortType = outputPort.getType();
			final Class<?> targetPortType = pipe.getTargetPort().getType();
			if (null == sourcePortType || !sourcePortType.equals(targetPortType)) {
				final InvalidPortConnection invalidPortConnection = new InvalidPortConnection(outputPort, pipe.getTargetPort());
				invalidPortConnections.add(invalidPortConnection);
			}
		}
	}

	@Override
	protected void terminate() {
		changeState(StageState.TERMINATING);
	}

	@Override
	protected boolean shouldBeTerminated() {
		return (getCurrentState() == StageState.TERMINATING);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return TerminationStrategy.BY_SIGNAL;
	}

	// protected <T> DynamicOutputPort<T> createDynamicOutputPort() {
	// final DynamicOutputPort<T> outputPort = new DynamicOutputPort<T>(null, this, outputPorts.size());
	// outputPorts.add(outputPort);
	// return outputPort;
	// }

	// protected <T> DynamicInputPort<T> createDynamicInputPort() {
	// final DynamicInputPort<T> inputPort = new DynamicInputPort<T>(null, this, inputPorts.size());
	// inputPorts.add(inputPort);
	// return inputPort;
	// }

	@Override
	protected void removeDynamicPort(final OutputPort<?> outputPort) {
		outputPorts.remove(outputPort); // TODO update setIndex IF it is still used
	}

	protected final void addOutputPortRemovedListener(final PortRemovedListener<OutputPort<?>> outputPortRemovedListener) {
		outputPorts.addPortRemovedListener(outputPortRemovedListener);
	}

	@Override
	protected void removeDynamicPort(final InputPort<?> inputPort) {
		inputPorts.remove(inputPort); // TODO update setIndex IF it is still used
	}

	protected final void addInputPortRemovedListener(final PortRemovedListener<InputPort<?>> inputPortRemovedListener) {
		inputPorts.addPortRemovedListener(inputPortRemovedListener);
	}

}
