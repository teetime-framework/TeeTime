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
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.AbstractExceptionListener.FurtherExecution;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.performancelogging.StateChange;
import teetime.framework.performancelogging.StateChange.StageActivationState;
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
public abstract class AbstractStage {

	private static final ConcurrentMap<String, Integer> INSTANCES_COUNTER = new ConcurrentHashMap<>();

	private static final Marker ON_STATE_CHANGE_MARKER = MarkerFactory.getMarker("ON_STATE_CHANGE_MARKER");

	private static final boolean PERFORMANCE_LOGGING_ENABLED = Boolean.getBoolean("performance.logging.enabled");

	/** This stage's unique logger */
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	/** This stage's unique identifier */
	private final String id;
	private AbstractExceptionListener exceptionListener;
	/** The owning thread of this stage if this stage is directly executed by an {@link AbstractRunnableStage}, <code>null</code> otherwise. */
	private Thread owningThread;
	private boolean isActive;
	/** the scheduler used for this stage and all of the other stages */
	private TeeTimeScheduler scheduler;

	private final Map<Class<? extends ISignal>, Set<InputPort<?>>> signalMap = new HashMap<>();
	private final Set<Class<? extends ISignal>> triggeredSignalTypes = new HashSet<>();

	private final PortList<InputPort<?>> inputPorts = new PortList<>();
	private final PortList<OutputPort<?>> outputPorts = new PortList<>();

	private boolean calledOnTerminating = false;
	private boolean calledOnStarting = false;

	private volatile StageState currentState = StageState.CREATED;
	/** used to detect termination */
	// private final AtomicInteger numOpenedInputPorts = new AtomicInteger();
	private int numOpenedInputPorts;

	// used only for performance measuring
	private long beforeExecuteTime;
	private long lastTimeAfterExecute;

	// for GlobalTaskQueueScheduling only
	/** producers start with a level index of 0. All other stages have an index > 0. */
	private int levelIndex = 0;
	// TODO used only by global task pool scheduling so far
	private final AtomicBoolean atomicBeingExecuted = new AtomicBoolean(false);
	private final AtomicBoolean atomicPaused = new AtomicBoolean(false);

	/**
	 * A list which save a timestamp and an associated state (active or inactive).
	 * This Information can be used for Bottleneck analysis.
	 */
	private final List<StateChange> states = new ArrayList<>();

	private StateChange lastState = new StateChange(StageActivationState.INITIALIZED, System.nanoTime());

	private long activeWaitingTime;
	/** indicates that this stage has no state and can thus be easily executed in parallel */
	private boolean stateless;

	protected AbstractStage() {
		this.id = this.createId();
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + ":" + id);
	}

	/**
	 * @param logger
	 *            a custom logger (potentially shared by multiple stage instances)
	 */
	protected AbstractStage(final Logger logger) {
		this.id = this.createId();
		this.logger = logger;
	}

	void setLevelIndex(final int levelIndex) {
		this.levelIndex = levelIndex;
	}

	int getLevelIndex() {
		return levelIndex;
	}

	public boolean isBeingExecuted() {
		return atomicBeingExecuted.get();
	}

	public boolean compareAndSetBeingExecuted(final boolean newValue) {
		return atomicBeingExecuted.compareAndSet(!newValue, newValue);
	}

	public void setPaused(final boolean newValue) {
		atomicPaused.set(newValue);
	}

	public boolean isPaused() {
		return atomicPaused.get();
	}

	/**
	 * @return an identifier that is unique among all stage instances. It is especially unique among all instances of the same stage type.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Required by {@link RuntimeServiceFacade#startWithinNewThread(AbstractStage, AbstractStage)}
	 */
	TeeTimeScheduler getScheduler() {
		return scheduler;
	}

	void setScheduler(final TeeTimeScheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Returns a string representation of this stage including its unique identifier.
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getId() + " [" + currentState + "]";
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

	/**
	 * This method is internally called by the framework.
	 * It must not be executed by user code.
	 * In particular, it must not be invoked from within any stage.
	 *
	 * @throws TerminateException
	 */
	public final void executeByFramework() throws TerminateException {
		if (PERFORMANCE_LOGGING_ENABLED) {
			beforeExecuteTime = System.nanoTime();
			executeWithCatchedExceptions();
			if (lastState.getStageActivationState() == StageActivationState.ACTIVE) {
				this.addActiveWaitingTime(beforeExecuteTime - lastTimeAfterExecute);
			}
			lastTimeAfterExecute = System.nanoTime();
		} else {
			executeWithCatchedExceptions();
		}
	}

	private void executeWithCatchedExceptions() throws TerminateException {
		try {
			this.execute();
		} catch (TerminateException e) {
			throw e;
		} catch (Exception e) {
			final FurtherExecution furtherExecution = this.exceptionListener.reportException(e, this);
			if (furtherExecution == FurtherExecution.TERMINATE) {
				throw TerminateException.INSTANCE;
			}
		}
	}

	/**
	 * Contains the logic of this stage and is invoked (possibly multiple times) by the framework.
	 *
	 * @throws Exception
	 *             arbitrary exception triggered by the logic of this stage
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected abstract void execute() throws Exception;

	// left commented out because:
	// without returnNoElement, it is now unknown when a consumer stage is busy-waiting.
	// We need a way to detect such a blocking behavior anyway.
	// only used by consumer stages
	// protected final void returnNoElement() {
	// // If the stage get null-element it can't be active. If it's the first time
	// // after being active the according time stamp is saved so that one can gather
	// // information about the time the stage was in one state uninterrupted.
	// if (newStateRequired(ExecutionState.BLOCKED)) {
	// this.addState(ExecutionState.BLOCKED, beforeExecuteTime);
	// }
	// throw NOT_ENOUGH_INPUT_EXCEPTION;
	// }

	// package-private would suffice, but protected is necessary for unit tests
	protected Thread getOwningThread() {
		return owningThread;
	}

	void setOwningThread(final Thread owningThread) {
		if (this.owningThread != null && this.owningThread != owningThread) { // NOPMD avoid empty if statement
			// checks also for "crossing threads"
			// throw new IllegalStateException("Attribute owningThread was set twice each with another thread");
		}
		this.owningThread = owningThread;
	}

	void setExceptionHandler(final AbstractExceptionListener exceptionHandler) {
		this.exceptionListener = exceptionHandler;
	}

	AbstractExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * Declares this stage to be executed by an own thread.
	 */
	public void declareActive() {
		if (getCurrentState() == StageState.STARTED) {
			// TODO implement so that active/passive can be changed even at runtime
			// requires: volatile isActive
			// requires: to declare further stages active (cascading)
			throw new UnsupportedOperationException("Declaring a stage 'active' at runtime is not yet supported.");
		}

		// serves as acknowledgement and thus must be set at the end
		this.isActive = true;
	}

	/**
	 * Declares this stage to be executed by the thread of its predecessor stage.
	 */
	public void declarePassive() {
		// TODO implement so that active/passive can be changed even at runtime
		// requires: to check whether this stage may be declared passive (a merger, e.g., is not allowed to do so in most cases)
		throw new UnsupportedOperationException("Declaring a stage 'passive' at runtime is not yet supported.");
		// this.isActive = false;
	}

	protected List<InputPort<?>> getInputPorts() {
		return inputPorts.getOpenedPorts(); // TODO consider to publish a read-only version
	}

	protected List<OutputPort<?>> getOutputPorts() {
		return outputPorts.getOpenedPorts(); // TODO consider to publish a read-only version
	}

	/**
	 * <i>This method is thread-safe.</i>
	 *
	 * @return the current state of this stage (one of {@link StageState})
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
			signalReceivedInputPorts = new HashSet<>();
			signalMap.put(signalClass, signalReceivedInputPorts);
		}

		if (!signalReceivedInputPorts.add(inputPort)) {
			this.logger.warn("Received more than one signal - {} - from input port: {}", signal, inputPort);
			return;
		}

		if (signal.mayBeTriggered(signalReceivedInputPorts, getInputPorts())) {
			signal.trigger(this);
			checkSuperCalls(signal);
			for (OutputPort<?> outputPort : outputPorts.getOpenedPorts()) {
				outputPort.sendSignal(signal);
			}
		}
	}

	private void checkSuperCalls(final ISignal signal) throws SuperNotCalledException {
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
		final StageState oldState = currentState;
		if (logger.isTraceEnabled()) {
			logger.trace(ON_STATE_CHANGE_MARKER, "Changing state from {} to {}", oldState, newState);
		}
		if (newState.compareTo(oldState) < 0) {
			throw new IllegalStateException(String.format("Illegal state change from %s to %s", oldState, newState));
		}
		currentState = newState;
	}

	/**
	 * Event that is triggered, if all of the following conditions hold:
	 * <ul>
	 * <li>after constructing this stage and
	 * <li>before starting the analysis.
	 * </ul>
	 * <p>
	 * If stage developers want to override this method, they must always call the super implementation first:
	 *
	 * <pre>
	 * &#64;Override
	 * public void onValidating() {
	 * 	super.onValidating();
	 * 	// insert your code here
	 * }
	 * </pre>
	 * <p>
	 * To throw a checked exception, wrap it to an unchecked exception, e.g. to an
	 * {@link IllegalArgumentException#IllegalArgumentException(String, Throwable)}.
	 * Always pass the original exception to the new unchecked exception to allow easy debugging.
	 *
	 * @param invalidPortConnections
	 */
	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		logger.trace(ON_STATE_CHANGE_MARKER, "Validating {}", this);
		this.checkTypeCompliance(invalidPortConnections);
		if (getScheduler() == null) {
			throw new IllegalStateException("A stage may not have a nullable scheduler.");
		}
		changeState(StageState.VALIDATED);
	}

	/**
	 * Event that is triggered, if all of the following conditions hold:
	 * <ul>
	 * <li>after passing the validation phase and
	 * <li>after the threads are ready-to-run and
	 * <li>just before the threads execute any stage.
	 * </ul>
	 * <p>
	 * If stage developers want to override this method, they must always call the super implementation first:
	 *
	 * <pre>
	 * &#64;Override
	 * protected void onStarting() {
	 * 	super.onStarting();
	 * 	// insert your code here
	 * }
	 * </pre>
	 * <p>
	 * To throw a checked exception, wrap it to an unchecked exception, e.g. to an
	 * {@link IllegalArgumentException#IllegalArgumentException(String, Throwable)}.
	 * Always pass the original exception to the new unchecked exception to allow easy debugging.
	 */
	protected void onStarting() {
		logger.trace(ON_STATE_CHANGE_MARKER, "Starting {}", this);
		changeState(StageState.STARTED);
		calledOnStarting = true;
	}

	/**
	 * Event that is triggered, if all of the following conditions hold:
	 * <ul>
	 * <li>while executing the P&amp;F configuration and
	 * <li>after receiving the termination signal.
	 * </ul>
	 * <p>
	 * If stage developers want to override this method, they must always call the super implementation last:
	 *
	 * <pre>
	 * &#64;Override
	 * protected void onTerminating() {
	 * 	// insert your code here
	 * 	super.onTerminating();
	 * }
	 * </pre>
	 * <p>
	 * To throw a checked exception, wrap it to an unchecked exception, e.g. to an {@link IllegalArgumentException#IllegalArgumentException(String, Throwable)}.
	 * Always pass the original exception to the new unchecked exception to allow easy debugging.
	 */
	protected void onTerminating() {
		logger.trace(ON_STATE_CHANGE_MARKER, "Terminating {}", this);
		if (newStateRequired(StageActivationState.TERMINATED)) {
			this.addState(StageActivationState.TERMINATED, System.nanoTime());
		}
		changeState(StageState.TERMINATED);
		calledOnTerminating = true;
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
		final InputPort<T> inputPort = new InputPort<>(type, this, name);
		inputPorts.add(inputPort);
		// numOpenedInputPorts.incrementAndGet();
		numOpenedInputPorts++;
		logger.debug("numOpenedInputPorts (inc): " + numOpenedInputPorts);
		return inputPort;
	}

	int decNumOpenedInputPorts() {
		// return numOpenedInputPorts.decrementAndGet();
		return --numOpenedInputPorts;
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
		final OutputPort<T> outputPort = new OutputPort<>(type, this, name);
		outputPorts.add(outputPort);
		return outputPort;
	}

	/**
	 * Terminates the execution of the stage. After terminating, this stage sends a signal to all its direct and indirect successor stages to terminate.
	 *
	 * @deprecated since 3.0. Use {@link #workCompleted()} instead.
	 */
	@Deprecated
	protected void terminateStage() {
		if (getInputPorts().size() != 0) {
			throw new UnsupportedOperationException("Consumer stages may not invoke this method.");
		}
		terminateStageByFramework();
	}

	/**
	 * Marks this stage as having finished its work such that it will not be scheduled anymore.
	 * The framework then automatically propagates a termination signal to the direct and indirect successor stages.
	 * In this way, each stage terminates itself after having processed all of its remaining input elements.
	 * Thus, the framework automatically terminates the whole P&amp;F configuration in a graceful way when all of its producer stages have finished their work.
	 * The user does not need to implement any additional or alternative termination logic.
	 * <p>
	 * This method may only be invoked by producers.
	 * Otherwise an {@link UnsupportedOperationException} is thrown.
	 *
	 * @since 3.0
	 */
	protected void workCompleted() {
		if (!isProducer()) {
			throw new UnsupportedOperationException("Consumer stages may not invoke this method.");
		}
		terminateStageByFramework();
	}

	/**
	 * Sets the current state of this stage to {@link StageState#TERMINATING}
	 */
	/* default */ void terminateStageByFramework() {
		changeState(StageState.TERMINATING);
	}

	protected void abort() { // invoked by ThreadService for all threadable stages
		this.terminateStageByFramework();
		this.getOwningThread().interrupt();
	}

	protected boolean shouldBeTerminated() {
		return (getCurrentState() == StageState.TERMINATING);
	}

	/**
	 * @deprecated since 3.0.
	 *             We will completely remove framework-backed support for infinite producers since it has never worked correctly in all (corner) cases.
	 *             Instead, please use finite producers and implement an appropriate termination condition by your own.
	 *
	 * @return the termination strategy of this stage
	 */
	@Deprecated
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

	List<StateChange> getStates() {
		return states;
	}

	private boolean newStateRequired(final StageActivationState state) {
		if (!PERFORMANCE_LOGGING_ENABLED) {
			return false;
		}
		return (this.lastState.getStageActivationState() != state);
	}

	private void addState(final StageActivationState stateCode, final long timestamp) {
		StateChange state = new StateChange(stateCode, timestamp);
		this.states.add(state);
		this.lastState = state;
	}

	void sendingFailed() {
		if (newStateRequired(StageActivationState.BLOCKED)) {
			this.addState(StageActivationState.BLOCKED, System.nanoTime());
		}
	}

	void sendingSucceeded() {
		if (newStateRequired(StageActivationState.ACTIVE)) {
			this.addState(StageActivationState.ACTIVE, System.nanoTime());
		}
	}

	long getActiveWaitingTime() {
		return this.activeWaitingTime;
	}

	void addActiveWaitingTime(final long time) {
		activeWaitingTime += time;
	}

	/**
	 * @return <code>true</code> iff this stage has no input ports, <code>false</code> otherwise.
	 */
	public boolean isProducer() {
		return inputPorts.size() == 0;
	}

	/**
	 * This method is used by some schedulers to improve parallelism and thus to improve the overall performance.
	 *
	 * @return <code>true</code> iff this stage has no internal fields which represent some kind of state; <code>false</code> otherwise.
	 */
	public boolean isStateless() {
		return stateless;
	}

	protected void setStateless(final boolean stateless) {
		this.stateless = stateless;
	}

	/**
	 * Only for testing purposes.
	 */
	static boolean isPerformanceLoggingEnabled() {
		return PERFORMANCE_LOGGING_ENABLED;
	}

}
