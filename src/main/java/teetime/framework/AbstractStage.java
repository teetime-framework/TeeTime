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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class AbstractStage extends Stage {

	private static final IPipe DUMMY_PIPE = new DummyPipe();

	private final Set<Class<? extends ISignal>> triggeredSignalTypes = new HashSet<Class<? extends ISignal>>();

	private InputPort<?>[] inputPorts = new InputPort<?>[0];
	private OutputPort<?>[] outputPorts = new OutputPort<?>[0];
	private StageState currentState = StageState.CREATED;

	private final Set<OutputPortRemovedListener> outputPortRemovedListeners = new HashSet<OutputPortRemovedListener>();
	private final Set<InputPortRemovedListener> inputPortsRemovedListeners = new HashSet<InputPortRemovedListener>();

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
		for (OutputPort<?> outputPort : this.outputPorts) {
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
		inputPorts = addElementToArray(inputPort, inputPorts);
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
		outputPorts = addElementToArray(outputPort, outputPorts);
		return outputPort;
	}

	private <T, E extends T> T[] addElementToArray(final E element, final T[] srcArray) {
		T[] newOutputPorts = Arrays.copyOf(srcArray, srcArray.length + 1);
		newOutputPorts[srcArray.length] = element;
		return newOutputPorts;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (OutputPort<?> outputPort : outputPorts) {
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

	protected <T> DynamicOutputPort<T> createDynamicOutputPort() {
		final DynamicOutputPort<T> outputPort = new DynamicOutputPort<T>(null, this, outputPorts.length);
		outputPorts = addElementToArray(outputPort, outputPorts);
		return outputPort;
	}

	@Override
	protected void removeDynamicPort(final DynamicOutputPort<?> dynamicOutputPort) {
		int index = dynamicOutputPort.getIndex();
		List<OutputPort<?>> tempOutputPorts = new ArrayList<OutputPort<?>>(Arrays.asList(outputPorts));
		OutputPort<?> removedOutputPort = tempOutputPorts.remove(index);
		for (int i = index; i < tempOutputPorts.size(); i++) {
			OutputPort<?> outputPort = tempOutputPorts.get(i);
			if (outputPort instanceof DynamicOutputPort) {
				((DynamicOutputPort<?>) outputPort).setIndex(i);
			}
		}
		outputPorts = tempOutputPorts.toArray(new OutputPort[0]);

		firePortRemoved(removedOutputPort);
	}

	private void firePortRemoved(final OutputPort<?> removedOutputPort) {
		for (OutputPortRemovedListener listener : outputPortRemovedListeners) {
			listener.onOutputPortRemoved(this, removedOutputPort);
		}
	}

	protected final void addOutputPortRemovedListener(final OutputPortRemovedListener outputPortRemovedListener) {
		outputPortRemovedListeners.add(outputPortRemovedListener);
	}

	@Override
	protected void removeDynamicPort(final DynamicInputPort<?> dynamicInputPort) {
		int index = dynamicInputPort.getIndex();
		List<InputPort<?>> tempInputPorts = new ArrayList<InputPort<?>>(Arrays.asList(inputPorts));
		InputPort<?> removedInputPort = tempInputPorts.remove(index);
		for (int i = index; i < tempInputPorts.size(); i++) {
			InputPort<?> inputPort = tempInputPorts.get(i);
			if (inputPort instanceof DynamicInputPort) {
				((DynamicInputPort<?>) inputPort).setIndex(i);
			}
		}
		inputPorts = tempInputPorts.toArray(new InputPort[0]);

		firePortRemoved(removedInputPort);
	}

	private void firePortRemoved(final InputPort<?> removedInputPort) {
		for (InputPortRemovedListener listener : inputPortsRemovedListeners) {
			listener.onInputPortRemoved(this, removedInputPort);
		}
	}

	protected final void addInputPortRemovedListener(final InputPortRemovedListener outputPortRemovedListener) {
		inputPortsRemovedListeners.add(outputPortRemovedListener);
	}

}
