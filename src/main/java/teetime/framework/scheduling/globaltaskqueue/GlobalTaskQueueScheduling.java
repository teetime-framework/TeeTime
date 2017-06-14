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
package teetime.framework.scheduling.globaltaskqueue;

import java.util.*;

import org.jctools.queues.MpmcArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.Traverser.Direction;
import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.signal.StartingSignal;

public class GlobalTaskQueueScheduling implements TeeTimeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalTaskQueueScheduling.class);
	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;
	private static final ConfigurationFacade CONFIG_FACADE = ConfigurationFacade.INSTANCE;

	private final List<AbstractStage> infiniteProducerStages = Collections.synchronizedList(new LinkedList<AbstractStage>());
	private final List<AbstractStage> finiteProducerStages = Collections.synchronizedList(new LinkedList<AbstractStage>());

	// TODO: Make unbounded or implement blocking for task creation
	private final MpmcArrayQueue<AbstractStage> taskQueue = new MpmcArrayQueue<AbstractStage>(100000);
	private final Map<AbstractStage, Boolean> runningStatefulStages = new HashMap<AbstractStage, Boolean>();
	private final Map<AbstractStage, List<StageBuffer>> stageList = Collections.synchronizedMap(new HashMap<AbstractStage, List<StageBuffer>>());

	private final int numThreads;
	private Configuration configuration;
	private final List<TeeTimeTaskQueueThreadChw> threadPool = new ArrayList<>();

	GlobalTaskQueueScheduling(final int numThreads) {
		this.numThreads = numThreads;
		// delay initialization of this.configuration
	}

	GlobalTaskQueueScheduling(final int numThreads, final Configuration configuration) {
		this.numThreads = numThreads;
		this.configuration = configuration;
	}

	public void setConfiguration(final Configuration configuration) {
		this.configuration = configuration;
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
			TeeTimeTaskQueueThreadChw thread = new TeeTimeTaskQueueThreadChw(this);
			threadPool.add(thread);
		}
	}

	private void initialize(final Collection<AbstractStage> startStages) {
		// TODO: Add port type validation again.
		if (startStages.isEmpty()) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		TaskQueueA1StageCollector stageCollector = new TaskQueueA1StageCollector();
		Traverser traversor = new Traverser(stageCollector, Direction.BOTH);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		Set<AbstractStage> allStages = stageCollector.getStages();

		for (AbstractStage stage : allStages) {
			categorizeStage(stage);
			setOwningThread(stage);
			setExceptionListener(stage);
			stageList.put(stage, new LinkedList<StageBuffer>());
		}

		if (finiteProducerStages.isEmpty() && infiniteProducerStages.isEmpty()) {
			throw new IllegalStateException("1004 - No producer stages in this configuration.");
		}

		TaskQueueA2PipeInstantiation pipeVisitor = new TaskQueueA2PipeInstantiation();
		traversor = new Traverser(pipeVisitor, Direction.BOTH);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}
	}

	private void categorizeStage(final AbstractStage stage) {
		switch (STAGE_FACADE.getTerminationStrategy(stage)) {
		case BY_INTERRUPT:
			infiniteProducerStages.add(stage);
			break;
		case BY_SELF_DECISION:
			finiteProducerStages.add(stage);
			break;
		case BY_SIGNAL:
			break;
		default:
			LOGGER.warn("Unknown termination strategy '{}' in stage {}", STAGE_FACADE.getTerminationStrategy(stage), stage);
			break;
		}
	}

	@Override
	public void onValidate() {
		// // BETTER validate concurrently
		// for (AbstractStage stage : threadableStages) {
		// final ValidatingSignal validatingSignal = new ValidatingSignal(); // NOPMD we need a new instance every iteration
		// stage.onSignal(validatingSignal, null);
		// if (validatingSignal.getInvalidPortConnections().size() > 0) {
		// throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
		// }
		// }
	}

	@Override
	public void onExecute() {
		synchronized (finiteProducerStages) {
			for (AbstractStage finiteProducerStage : finiteProducerStages) {
				finiteProducerStage.onSignal(new StartingSignal(), null);
			}
		}
		synchronized (infiniteProducerStages) {
			for (AbstractStage infiniteProducerStage : infiniteProducerStages) {
				infiniteProducerStage.onSignal(new StartingSignal(), null);
			}
		}
		for (Thread thread : threadPool) {
			thread.start();
		}
	}

	@Override
	public void onTerminate() {
		synchronized (finiteProducerStages) {
			for (AbstractStage finiteProducerStage : finiteProducerStages) {
				STAGE_FACADE.abort(finiteProducerStage);
			}
		}
		synchronized (infiniteProducerStages) {
			for (AbstractStage infiniteProducerStage : infiniteProducerStages) {
				STAGE_FACADE.abort(infiniteProducerStage);
			}
		}
	}

	@Override
	public void onFinish() {
		try {
			for (Thread thread : threadPool) {
				thread.join();
			}
		} catch (InterruptedException e) {
			LOGGER.error("Execution has stopped unexpectedly", e);
			LOGGER.debug("Interrupting infiniteProducerThreads...");
			for (Thread thread : threadPool) {
				thread.interrupt();
			}
			LOGGER.debug("infiniteProducerThreads have been terminated");
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

	// Nothing has to be done here
	void startStageAtRuntime(final AbstractStage stage) {

	}

	public List<AbstractStage> getInfiniteProducerStages() {
		return infiniteProducerStages;
	}

	public List<AbstractStage> getFiniteProducerStages() {
		return finiteProducerStages;
	}

	public MpmcArrayQueue<AbstractStage> getTaskQueue() {
		return taskQueue;
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

	public Map<AbstractStage, List<StageBuffer>> getStageList() {
		return stageList;
	}
}
