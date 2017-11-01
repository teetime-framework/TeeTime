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
package teetime.framework.scheduling.globaltaskpool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.jctools.util.Pow2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.AbstractUnsynchedPipe;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.scheduling.PipeScheduler;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;

/**
 * This scheduling approach maintains a global, synchronized, prioritized task pool whose tasks are stages.
 * Multiple threads access this task pool concurrently.
 * At each moment in time, a particular stage is executed only by at most one thread.
 * Thus, a stage in combination with the task data structure acts as a lock for executing that stage.
 *
 * @author Christian Wulf (chw)
 *
 * @since 3.0
 *
 */
public class GlobalTaskPoolScheduling implements TeeTimeService, PipeScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalTaskPoolScheduling.class);
	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;
	private static final ConfigurationFacade CONFIG_FACADE = ConfigurationFacade.INSTANCE;
	private static final int DEFAULT_NUM_OF_EXECUTIONS = 1;

	/** (synchronized) */
	private final List<AbstractStage> finiteProducerStages = Collections.synchronizedList(new LinkedList<AbstractStage>());
	/** Contains all stages which have no predecessors or only terminated predecessors (synchronized) */
	private final Set<AbstractStage> frontStages = ConcurrentHashMap.newKeySet();

	/**
	 * Holds all stages which should be executed next. A stage instance can occur more than once in this pool.
	 * <br>
	 * <i>(synchronized pool)</i>
	 */
	private PrioritizedTaskPool taskPool;
	/** (not synchronized) */
	private final Map<AbstractStage, Boolean> runningStatefulStages = new HashMap<AbstractStage, Boolean>();
	/** (synchronized) */
	private final Map<AbstractStage, List<StageBuffer>> stageList = Collections.synchronizedMap(new HashMap<AbstractStage, List<StageBuffer>>());

	private final int numThreads;
	/** the number of execution per scheduled stage (always a power of two) */
	private final int actualNumOfExecutions;
	private final int numOfExecutionsMask;
	/** the configuration to execute/schedule */
	private final Configuration configuration;
	/** Holds all threads which are used to execute the stages */
	private final List<TeeTimeTaskQueueThreadChw> threadPool = new ArrayList<>();
	private final CountDownAndUpLatch numRunningStages = new CountDownAndUpLatch();
	private final List<TeeTimeTaskQueueThreadChw> backupThreads = Collections.synchronizedList(new ArrayList<>());
	/** synchronized */
	private final Set<AbstractStage> pausedStages = ConcurrentHashMap.newKeySet();
	private final Map<AbstractStage, Semaphore> stagePermissions = new ConcurrentHashMap<>();

	/**
	 * A thread executes a stage {@value #DEFAULT_NUM_OF_EXECUTIONS}x per job.
	 *
	 * @param numThreads
	 * @param configuration
	 */
	public GlobalTaskPoolScheduling(final int numThreads, final Configuration configuration) {
		this(numThreads, configuration, DEFAULT_NUM_OF_EXECUTIONS);
	}

	/**
	 * @param numThreads
	 *            the number of threads to use for executing the given P&F configuration
	 * @param configuration
	 *            the configuration to execute/schedule
	 * @param numOfExecutions
	 *            the number of execution per scheduled stage (task) for a thread. Is rounded up to the next power of 2, i.e., <code>1,2,4,16,...</code>
	 */
	public GlobalTaskPoolScheduling(final int numThreads, final Configuration configuration, final int numOfExecutions) {
		this.numThreads = numThreads;
		this.configuration = configuration;
		int actualNumOfExecutions = Pow2.roundToPowerOfTwo(numOfExecutions);
		this.actualNumOfExecutions = actualNumOfExecutions;
		this.numOfExecutionsMask = actualNumOfExecutions - 1;
	}

	// 1. initializeServices
	// 2. validateServices
	// 3. executeConfiguration
	// (4. abortConfigurationRun)
	// 5. waitForConfigurationToTerminate

	@Override
	public void onInitialize() {
		Collection<AbstractStage> startStages = CONFIG_FACADE.getStartStages(configuration);
		initialize(startStages);

		// Add threads to thread pool and start
		for (int i = 0; i < numThreads; i++) {
			TeeTimeTaskQueueThreadChw thread = new TeeTimeTaskQueueThreadChw(this, actualNumOfExecutions);
			LOGGER.debug("Starting {}", thread.getName());
			thread.start();
			threadPool.add(thread);
		}
	}

	private void initialize(final Collection<AbstractStage> startStages) {
		// TODO: Add port type validation again.
		if (startStages.isEmpty()) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		TaskQueueA1StageCollector stageCollector = new TaskQueueA1StageCollector();
		Traverser traversor = new Traverser(stageCollector);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		Set<AbstractStage> allStages = stageCollector.getStages();

		for (AbstractStage stage : allStages) {
			categorizeStage(stage);
			setOwningThread(stage);
			setExceptionListener(stage);
			setScheduler(stage);
			stageList.put(stage, new LinkedList<StageBuffer>());
		}

		if (finiteProducerStages.isEmpty()) {
			throw new IllegalStateException("1004 - No producer stages in this configuration.");
		}

		// compute level index for each stage
		LevelIndexVisitor levelIndexVisitor = new LevelIndexVisitor();
		traversor = new Traverser(levelIndexVisitor);
		for (AbstractStage startStage : finiteProducerStages) {
			traversor.traverse(startStage);
		}

		taskPool = new PrioritizedTaskPool(levelIndexVisitor.getMaxLevelIndex() + 1);
		taskPool.scheduleStages(frontStages);

		initializeBackupThreads(allStages.size() /*- numThreads*/);

		// instantiate pipes
		TaskQueueA2PipeInstantiation pipeVisitor = new TaskQueueA2PipeInstantiation(this);
		traversor = new Traverser(pipeVisitor);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}
	}

	private void initializeBackupThreads(final int size) {
		for (int i = 0; i < size; i++) {
			TeeTimeTaskQueueThreadChw backupThread = new TeeTimeTaskQueueThreadChw(this, actualNumOfExecutions);
			backupThread.setName(backupThread.getName() + "-backup");
			backupThread.start();
			backupThreads.add(backupThread);
		}
	}

	private void categorizeStage(final AbstractStage stage) {
		switch (STAGE_FACADE.getTerminationStrategy(stage)) {
		case BY_INTERRUPT:
			throw new IllegalStateException("Infinite producers are not supported by this scheduling strategy.");
		case BY_SELF_DECISION:
			finiteProducerStages.add(stage);
			frontStages.add(stage);
			numRunningStages.countUp();
			break;
		case BY_SIGNAL:
			numRunningStages.countUp();
			break;
		default:
			LOGGER.warn("Unknown termination strategy '{}' in stage {}", STAGE_FACADE.getTerminationStrategy(stage), stage);
			break;
		}
	}

	@Override
	public void onValidate() {
		// // BETTER validate concurrently
		final ValidatingSignal signal = new ValidatingSignal();
		SignalVisitor signalVisitor = new SignalVisitor(signal);
		BreadthFirstTraverser traversor = new BreadthFirstTraverser();

		synchronized (frontStages) {
			for (AbstractStage stage : frontStages) {
				traversor.traverse(stage, signalVisitor);

				if (signal.getInvalidPortConnections().size() > 0) {
					throw new AnalysisNotValidException(signal.getInvalidPortConnections());
				}
			}
		}
	}

	@Override
	public void onExecute() {
		final StartingSignal signal = new StartingSignal();
		SignalVisitor signalVisitor = new SignalVisitor(signal);
		BreadthFirstTraverser traversor = new BreadthFirstTraverser();

		synchronized (frontStages) {
			for (AbstractStage stage : frontStages) {
				traversor.traverse(stage, signalVisitor);
			}
		}

		// TODO move before onExecute so that starting the threads does not count to the execution time #350
		for (TeeTimeTaskQueueThreadChw thread : threadPool) {
			thread.awake();
		}
	}

	@Override
	public void onTerminate() {
		synchronized (frontStages) {
			for (AbstractStage finiteProducerStage : frontStages) {
				STAGE_FACADE.abort(finiteProducerStage);
			}
		}
	}

	@Override
	public void onFinish() {
		numRunningStages.await();

		LOGGER.debug("Finished execution.");

		try {
			for (TeeTimeTaskQueueThreadChw thread : threadPool) {
				thread.awake();
				thread.join();
			}
			for (TeeTimeTaskQueueThreadChw thread : backupThreads) {
				thread.awake();
				thread.join();
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		// List<Exception> exceptions = collectExceptions();
		// if (!exceptions.isEmpty()) {
		// throw new ExecutionException(exceptions);
		// }
	}

	// TODO impl throw exception... see line 175
	// private List<Exception> collectExceptions() {
	// Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();
	// List<Exception> exceptions = new ArrayList<Exception>();
	//
	// for (Stage stage : threadableStages.keySet()) {
	// List<Exception> stageExceptions = stage.exceptionListener.getExceptions();
	// exceptions.addAll(stageExceptions);
	// }
	//
	// return exceptions;
	// }

	// @Override
	// void merge(final ThreadService source) {
	// threadableStages.putAll(source.getThreadableStages());
	// runnableCounter.inc(source.runnableCounter);
	// }

	@Override
	public void startStageAtRuntime(final AbstractStage stage) {
		// Nothing has to be done here
	}

	public List<AbstractStage> getFiniteProducerStages() {
		return finiteProducerStages;
	}

	public Set<AbstractStage> getFrontStages() {
		return frontStages;
	}

	public PrioritizedTaskPool getPrioritizedTaskPool() {
		return taskPool;
	}

	public Map<AbstractStage, Boolean> getRunningStatefulStages() {
		return runningStatefulStages;
	}

	public void setOwningThread(final AbstractStage stage) {
		STAGE_FACADE.setOwningThread(stage, Thread.currentThread());
	}

	private void setExceptionListener(final AbstractStage stage) {
		AbstractExceptionListener handler = CONFIG_FACADE.getFactory(configuration).createInstance(Thread.currentThread());
		STAGE_FACADE.setExceptionHandler(stage, handler);
	}

	private void setScheduler(final AbstractStage stage) {
		STAGE_FACADE.setScheduler(stage, this);
	}

	public Map<AbstractStage, List<StageBuffer>> getStageList() {
		return stageList;
	}

	/* default */ CountDownAndUpLatch getNumRunningStages() {
		return numRunningStages;
	}

	@Override
	public void onElementAdded(final AbstractUnsynchedPipe<?> pipe) {
		String message = String.format("This scheduler does not allow unsynched pipes: %s", pipe);
		throw new IllegalStateException(message);
	}

	@Override
	public void onElementAdded(final AbstractSynchedPipe<?> pipe) {
		// TODO consider to pass IMonitorablePipe as parameter type
		IMonitorablePipe monitorablePipe = (IMonitorablePipe) pipe;
		long numPushes = monitorablePipe.getNumPushesSinceAppStart();
		// performance optimization: & represents % (modulo)
		if ((numPushes & numOfExecutionsMask) != 0) {
			// TODO numPushes is wrong. It must be diff=(numPushes-numPushesAtBeginExecution).
			// throw new IllegalStateException("numPushes: " + numPushes); // for debugging purposes with numOfExecutionsMask==1 FIXME remove
			return;
		}
		AbstractStage targetStage = pipe.getCachedTargetStage();
		// LOGGER.debug("Scheduling stage {}: {}", targetStage, numPushes);
		if (targetStage.getCurrentState().compareTo(StageState.TERMINATING) < 0) {
			// TeeTimeTaskQueueThreadChw currentThread = (TeeTimeTaskQueueThreadChw) Thread.currentThread();

			AbstractStage sourceStage = pipe.getSourcePort().getOwningStage();
			// boolean yield = false;
			while (!taskPool.scheduleStage(targetStage)) {
				// LOGGER.debug("Yielding {} cause of full pool level {} triggered after {} pushes", sourceStage, targetStage.getLevelIndex(),
				// monitorablePipe.getNumPushesSinceAppStart());
				// yield = true;
				// this.yieldStage(sourceStage);
				throw new IllegalStateException("Could not schedule " + targetStage + "\n" + taskPool);
			}

			// always yield stage
			this.yieldStage(sourceStage);

			// if (!taskPool.scheduleStage(targetStage)) {
			// throw new IllegalStateException("Could not schedule " + targetStage + "\n" + taskPool);
			// }
			// if (yield) {
			// LOGGER.debug("Continue {}", sourceStage);
			// }

			// AbstractStage runningStage = pipe.getSourcePort().getOwningStage();
			// must release the lock at this point (handled by processNextStage)
			// currentThread.processNextStage(taskPool, runningStage);
			// if the pool returns null, the current stage continues with adding and thus overflows the pipe
			// must regain the lock at this point (handled by processNextStage)
		}
	}

	/**
	 * Among others, pauses the executing thread.
	 *
	 * @param stage
	 */
	void yieldStage(final AbstractStage stage) {
		// awake any backup thread
		backupThreads.remove(0).awake();

		while (!taskPool.scheduleStage(stage)) {
			throw new IllegalStateException(String.format("(yieldStage) Self-scheduling failed for %s", stage));
		}

		stage.setPaused(true);
		// allow other to execute the stage (only) in order to awake the current thread again
		setIsBeingExecuted(stage, false);

		getCurrentThread().pause();

		if (!stage.isBeingExecuted()) {
			throw new IllegalStateException("Stage must be in state 'is being executed'");
		}

		stage.setPaused(false);
		LOGGER.debug("Continue with {}", stage);
	}

	private TeeTimeTaskQueueThreadChw getCurrentThread() {
		return (TeeTimeTaskQueueThreadChw) Thread.currentThread();
	}

	/**
	 * Revokes the stage's pause and pauses the current thread afterwards.
	 *
	 * @param stage
	 */
	void continueStage(final AbstractStage stage) {
		TeeTimeTaskQueueThreadChw thisThread = getCurrentThread();
		backupThreads.add(thisThread);

		if (!stage.isBeingExecuted()) {
			throw new IllegalStateException("Stage must be in state 'is being executed'");
		}

		/* must follow "backupThreads.add" so that the awakened thread can invoke "backupThreads.remove(0)" without causing an IndexOutOfBoundsException */
		TeeTimeTaskQueueThreadChw owningThread = this.getOwningThreadSynched(stage);
		owningThread.awake();

		thisThread.pause();
		LOGGER.debug("Continue (backup) with {}", stage);
	}

	public boolean isPausedStage(final AbstractStage stage) {
		return stage.isPaused();
	}

	public boolean isBeingExecuted(final AbstractStage stage) {
		return stage.isBeingExecuted();
	}

	public boolean setIsBeingExecuted(final AbstractStage stage, final boolean newValue) {
		return stage.compareAndSetBeingExecuted(newValue);
	}

	public void setOwningThreadSynced(final AbstractStage stage, final TeeTimeTaskQueueThreadChw newThread) {
		synchronized (stage) {
			STAGE_FACADE.setOwningThread(stage, newThread);
		}
	}

	private TeeTimeTaskQueueThreadChw getOwningThreadSynched(final AbstractStage stage) {
		synchronized (stage) {
			return (TeeTimeTaskQueueThreadChw) STAGE_FACADE.getOwningThread(stage);
		}
	}

}