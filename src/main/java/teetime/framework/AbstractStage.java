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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.AbstractExceptionListener.FurtherExecution;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.performancelogging.StateChange;
import teetime.framework.performancelogging.StateChange.ExecutionState;
import teetime.framework.performancelogging.StateLoggable;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.util.framework.port.PortList;
import teetime.util.framework.port.PortRemovedListener;

/**
 * Represents a minimal Stage, with some pre-defined methods.
 * Implemented stages need to adapt all abstract methods with own implementations.
 */
public abstract class AbstractStage implements StateLoggable {

	private static final ConcurrentMap<String, Integer> INSTANCES_COUNTER = new ConcurrentHashMap<String, Integer>();
	private static final NotEnoughInputException NOT_ENOUGH_INPUT_EXCEPTION = new NotEnoughInputException();

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	private AbstractExceptionListener exceptionListener;

	/** The owning thread of this stage if this stage is directly executed by a {@link AbstractRunnableStage}, <code>null</code> otherwise. */
	private Thread owningThread;

	private boolean isActive;

	private ConfigurationContext owningContext;

	ConfigurationContext getOwningContext() {
		return owningContext;
	}

	void setOwningContext(final ConfigurationContext owningContext) {
		this.owningContext = owningContext;
	}

	protected AbstractStage() {
		this.id = this.createId();
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + ":" + id);
	}

	/**
	 * @return an identifier that is unique among all stage instances. It is especially unique among all instances of the same stage type.
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getId();
	}

	private String createId() {
		String simpleName = this.getClass().getSimpleName();

		Integer numInstances = INSTANCES_COUNTER.get(simpleName);
		if (null == numInstances) {
			numInstances = 0;
		}

		String newId = simpleName + "-" + numInstances;
		INSTANCES_COUNTER.put(simpleName, ++numInstances);
		return newId;
	}

	static void clearInstanceCounters() {
		INSTANCES_COUNTER.clear();
	}

	// only used by consumer stages
	protected final void returnNoElement() {
		// If the stage get null-element it can't be active. If it's the first time
		// after being active the according time stamp is saved so that one can gather
		// information about the time the stage was in one state uninterrupted.
		if (newStateRequired(ExecutionState.BLOCKED)) {
			StateChange newState = new StateChange(ExecutionState.BLOCKED, this.getActualTimeStamp(), StateChange.PULLING_FAILED);
			this.addState(newState);
		}
		throw NOT_ENOUGH_INPUT_EXCEPTION;
	}

	protected final void executeStage() {
		if (performanceLoggingEnabled) {
			this.setActualTimeStamp(System.nanoTime());
		}

		try {
			this.execute();
		} catch (NotEnoughInputException e) {
			throw e;
		} catch (TerminateException e) {
			throw e;
		} catch (Exception e) {
			final FurtherExecution furtherExecution = this.exceptionListener.reportException(e, this);
			if (furtherExecution == FurtherExecution.TERMINATE) {
				throw TerminateException.INSTANCE;
			}
		}
	}

	protected abstract void execute();

	// package-private would suffice, but protected is necessary for unit tests
	protected Thread getOwningThread() {
		return owningThread;
	}

	void setOwningThread(final Thread owningThread) {
		if (this.owningThread != null && this.owningThread != owningThread) {
			// checks also for "crossing threads"
			// throw new IllegalStateException("Attribute owningThread was set twice each with another thread");
		}
		this.owningThread = owningThread;
	}

	// events

	protected final void setExceptionHandler(final AbstractExceptionListener exceptionHandler) {
		this.exceptionListener = exceptionHandler;
	}

	protected AbstractExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * Declares this stage to be executed in an own thread.
	 */
	public void declareActive() {
		this.isActive = true;
		// AbstractStage threadableStage = this;
		// AbstractRunnableStage runnable = AbstractRunnableStage.create(threadableStage);
		// Thread newThread = new TeeTimeThread(runnable, "Thread for " + threadableStage.getId());
		// threadableStage.setOwningThread(newThread);
	}

	private final Map<Class<? extends ISignal>, Set<InputPort<?>>> signalMap = new HashMap<Class<? extends ISignal>, Set<InputPort<?>>>();
	private final Set<Class<? extends ISignal>> triggeredSignalTypes = new HashSet<Class<? extends ISignal>>();

	private final PortList<InputPort<?>> inputPorts = new PortList<InputPort<?>>();
	private final PortList<OutputPort<?>> outputPorts = new PortList<OutputPort<?>>();

	private boolean calledOnTerminating = false;
	private boolean calledOnStarting = false;

	private volatile StageState currentState = StageState.CREATED;

	protected List<InputPort<?>> getInputPorts() {
		return inputPorts.getOpenedPorts(); // TODO consider to publish a read-only version
	}

	protected List<OutputPort<?>> getOutputPorts() {
		return outputPorts.getOpenedPorts(); // TODO consider to publish a read-only version
	}

	/**
	 * @threadsafe
	 */
	public StageState getCurrentState() {
		return currentState;
	}

	/**
	 * May not be invoked outside of IPipe implementations
	 *
	 * @param signal
	 *            The incoming signal
	 *
	 * @param inputPort
	 *            The port which received the signal
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		Class<? extends ISignal> signalClass = signal.getClass();

		Set<InputPort<?>> signalReceivedInputPorts;
		if (signalMap.containsKey(signalClass)) {
			signalReceivedInputPorts = signalMap.get(signalClass);
		} else {
			signalReceivedInputPorts = new HashSet<InputPort<?>>();
			signalMap.put(signalClass, signalReceivedInputPorts);
		}

		if (!signalReceivedInputPorts.add(inputPort)) {
			this.logger.warn("Received more than one signal - " + signal + " - from input port: " + inputPort);
			return;
		}

		if (signal.mayBeTriggered(signalReceivedInputPorts, getInputPorts())) {
			try {
				signal.trigger(this);
				checkSuperCalls(signal);
			} catch (Exception e) {
				this.logger.error("Could not trigger signal.", e);
				this.getOwningContext().abortConfigurationRun();
			}
			for (OutputPort<?> outputPort : outputPorts.getOpenedPorts()) {
				outputPort.sendSignal(signal);
			}
		}
	}

	private void checkSuperCalls(final ISignal signal) {
		if (signal instanceof StartingSignal) {
			if (!calledOnStarting) {
				throw new SuperNotCalledException("The super method onStarting was not called in " + this.getId());
			}
		}
		if (signal instanceof TerminatingSignal) {
			if (!calledOnTerminating) {
				throw new SuperNotCalledException("The super method onTerminating was not called in " + this.getId());
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
		final boolean signalAlreadyReceived = this.triggeredSignalTypes.contains(signal.getClass());
		if (signalAlreadyReceived) {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal again: {} from input port: {}", signal, inputPort);
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Got signal: {} from input port: {}", signal, inputPort);
			}
			this.triggeredSignalTypes.add(signal.getClass());
		}
		return signalAlreadyReceived;
	}

	private void changeState(final StageState newState) {
		currentState = newState;
		if (logger.isTraceEnabled()) {
			logger.trace(newState.toString());
		}
	}

	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		this.checkTypeCompliance(invalidPortConnections);
		changeState(StageState.VALIDATED);
	}

	/**
	 * Event that is triggered within the initialization phase of the analysis.
	 * It does not count to the execution time.
	 *
	 * @throws Exception
	 *             an arbitrary exception if an error occurs during the initialization
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void onStarting() throws Exception {
		logger.debug("Stage {} within thread {}", getId(), getOwningThread().getId());
		changeState(StageState.STARTED);
		calledOnStarting = true;
	}

	/**
	 * Checks if connections to this pipe are correct in regards to type compliance.
	 * Incoming elements must be instanceof input port type.
	 *
	 * @param invalidPortConnections
	 *            List of invalid connections. Adding invalid connections to this list is a performance advantage in comparison to returning a list by each stage.
	 */
	private void checkTypeCompliance(final List<InvalidPortConnection> invalidPortConnections) {
		for (InputPort<?> port : getInputPorts()) {
			Class<?> targetType = port.getType();
			Class<?> sourceType = port.pipe.getSourcePort().getType();
			if (targetType != null && sourceType != null) {
				if (!targetType.isAssignableFrom(sourceType)) { // if targetType is not superclass of sourceType
					invalidPortConnections.add(new InvalidPortConnection(port.pipe.getSourcePort(), port));
					// throw new IllegalStateException("2002 - Invalid pipe at " + port.toString() + ": " + targetType + " is not a superclass/type of " +
					// sourceType);
				}
			}
		}
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void onTerminating() throws Exception {
		this.addState(ExecutionState.TERMINATED);
		changeState(StageState.TERMINATED);
		calledOnTerminating = true;
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

	/**
	 * @deprecated As of v2.1. Use {@link #terminateStage()} instead.
	 */
	@Deprecated
	protected void terminate() {
		this.terminateStage();
	}

	/**
	 * Terminates the execution of the stage. After terminating, this stage sends a signal to all its direct and indirect successor stages to terminate.
	 */
	protected void terminateStage() {
		changeState(StageState.TERMINATING);
	}

	protected void abort() {
		this.terminateStage();
		this.getOwningThread().interrupt();
	}

	protected boolean shouldBeTerminated() {
		return (getCurrentState() == StageState.TERMINATING);
	}

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

	protected void removeDynamicPort(final OutputPort<?> outputPort) {
		outputPorts.remove(outputPort); // TODO update setIndex IF it is still used
	}

	protected void removeDynamicPort(final InputPort<?> inputPort) {
		inputPorts.remove(inputPort); // TODO update setIndex IF it is still used
	}

	protected final void addOutputPortRemovedListener(final PortRemovedListener<OutputPort<?>> outputPortRemovedListener) {
		outputPorts.addPortRemovedListener(outputPortRemovedListener);
	}

	protected final void addInputPortRemovedListener(final PortRemovedListener<InputPort<?>> inputPortRemovedListener) {
		inputPorts.addPortRemovedListener(inputPortRemovedListener);
	}

	//
	// /**
	// * This should check, if the OutputPorts are connected correctly. This is needed to avoid NullPointerExceptions and other errors.
	// *
	// * @param invalidPortConnections
	// * <i>(Passed as parameter for performance reasons)</i>
	// */
	// public abstract void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);

	/**
	 * A list which save a timestamp and an associated state (active or inactive). This Information can be used for Bottleneck analysis.
	 *
	 */
	private final List<StateChange> states = new ArrayList<StateChange>();

	private StateChange lastState = new StateChange(ExecutionState.INITIALIZED);

	private long actualTimeStamp;

	/**
	 * Deactivated if performance logging does not reduce the performance. must be measured first. (28.10.2016)
	 */
	private final boolean performanceLoggingEnabled = false;

	@Override
	public List<StateChange> getStates() {
		return states;
	}

	protected void addState(final ExecutionState stateCode) {
		StateChange state = new StateChange(stateCode);
		addState(state);
	}

	protected void addState(final StateChange state) {
		this.states.add(state);
		this.lastState = state;
	}

	protected boolean newStateRequired(final ExecutionState state) {
		if (!performanceLoggingEnabled) {
			return false;
		}
		return (this.lastState.getExecutionState() != state);
	}

	@Override
	public void sendingFailed() {
		if (newStateRequired(ExecutionState.BLOCKED)) {
			this.addState(new StateChange(ExecutionState.BLOCKED, StateChange.SENDING_FAILED));
		}
	}

	@Override
	public void sendingSucceeded() {
		if (newStateRequired(ExecutionState.ACTIVE_WAITING)) {
			this.addState(ExecutionState.ACTIVE_WAITING);
		}
	}

	@Override
	public void sendingReturned() {
		if (newStateRequired(ExecutionState.ACTIVE)) {
			this.addState(ExecutionState.ACTIVE);
		}
	}

	public long getActualTimeStamp() {
		return actualTimeStamp;
	}

	public void setActualTimeStamp(final long actualTimeStamp) {
		this.actualTimeStamp = actualTimeStamp;
	}

}
