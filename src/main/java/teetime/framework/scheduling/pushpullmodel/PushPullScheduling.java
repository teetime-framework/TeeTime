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
package teetime.framework.scheduling.pushpullmodel;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.scheduling.CountDownAndUpLatch;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;

public class PushPullScheduling implements TeeTimeScheduler, ThreadListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(PushPullScheduling.class);

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;
	private static final ConfigurationFacade CONFIG_FACADE = ConfigurationFacade.INSTANCE;

	private final List<Thread> consumerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> finiteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> infiniteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());

	private final Set<AbstractStage> threadableStages = Collections.synchronizedSet(new HashSet<AbstractStage>());

	private final Configuration configuration;

	private final CountDownAndUpLatch numRunningFiniteProducers = new CountDownAndUpLatch();
	private final CountDownAndUpLatch numRunningConsumers = new CountDownAndUpLatch();

	// requires: startstages, factory and context
	public PushPullScheduling(final Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void onInitialize() {
		Collection<AbstractStage> startStages = CONFIG_FACADE.getStartStages(configuration);

		Set<AbstractStage> newThreadableStages = initialize(startStages);
		startThreads(newThreadableStages);
	}

	@Override
	public void startStageAtRuntime(final AbstractStage newStage) {
		newStage.declareActive();
		List<AbstractStage> newStages = Arrays.asList(newStage);

		Set<AbstractStage> newThreadableStages = initialize(newStages);
		startThreads(newThreadableStages);

		// FIXME remove this hack and find a consistent solution
		if (newStage.isProducer()) {
			ValidatingSignal validatingSignal = new ValidatingSignal();
			newStage.onSignal(validatingSignal, null);
			if (validatingSignal.getInvalidPortConnections().size() > 0) {
				throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
			}
			sendStartingSignal(newThreadableStages);
		}
	}

	// extracted for runtime use
	private Set<AbstractStage> initialize(final Collection<AbstractStage> startStages) {
		if (startStages.isEmpty()) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		A1ThreadableStageCollector stageCollector = new A1ThreadableStageCollector();
		Traverser traversor = new Traverser(stageCollector);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		Set<AbstractStage> newThreadableStages = stageCollector.getThreadableStages();

		threadableStages.addAll(newThreadableStages);
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("1004 - No threadable stages in this configuration.");
		}

		A2InvalidThreadAssignmentCheck checker = new A2InvalidThreadAssignmentCheck(newThreadableStages);
		checker.check();

		A3PipeInstantiation pipeVisitor = new A3PipeInstantiation();
		traversor = new Traverser(pipeVisitor);
		for (AbstractStage startStage : startStages) {
			traversor.traverse(startStage);
		}

		A4StageAttributeSetter attributeSetter = new A4StageAttributeSetter(configuration, newThreadableStages, this);
		attributeSetter.setAttributes();

		for (AbstractStage stage : newThreadableStages) {
			categorizeThreadableStage(stage);
			// watchTerminationThread.addConsumerStage(stage);
		}

		return newThreadableStages;
	}

	private void categorizeThreadableStage(final AbstractStage stage) {
		TerminationStrategy terminationStrategy = STAGE_FACADE.getTerminationStrategy(stage);

		switch (terminationStrategy) {
		case BY_INTERRUPT: {
			Thread thread = STAGE_FACADE.getOwningThread(stage);
			infiniteProducerThreads.add(thread);
			break;
		}
		case BY_SELF_DECISION: {
			Thread thread = STAGE_FACADE.getOwningThread(stage);
			finiteProducerThreads.add(thread);
			break;
		}
		case BY_SIGNAL: {
			Thread thread = STAGE_FACADE.getOwningThread(stage);
			consumerThreads.add(thread);
			break;
		}
		default:
			LOGGER.warn("Unknown termination strategy '{}' in stage {}", terminationStrategy, stage);
			break;
		}
	}

	private void startThreads(final Set<AbstractStage> threadableStages) {
		for (AbstractStage stage : threadableStages) {
			TeeTimeThread thread = (TeeTimeThread) STAGE_FACADE.getOwningThread(stage);
			thread.setListener(this);
			thread.start();
		}
	}

	private void sendStartingSignal(final Set<AbstractStage> newThreadableStages) {
		// TODO why synchronized?
		synchronized (newThreadableStages) {
			for (AbstractStage stage : newThreadableStages) {
				((TeeTimeThread) STAGE_FACADE.getOwningThread(stage)).sendStartingSignal();
			}
		}
	}

	@Override
	public void onValidate() {
		// BETTER validate concurrently
		for (AbstractStage stage : threadableStages) {
			final ValidatingSignal validatingSignal = new ValidatingSignal(); // NOPMD we need a new instance every iteration
			stage.onSignal(validatingSignal, null);
			if (validatingSignal.getInvalidPortConnections().size() > 0) {
				throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
			}
		}
	}

	@Override
	public void onExecute() {
		sendStartingSignal(threadableStages);
	}

	@Override
	public void onTerminate() {
		abortStages(threadableStages);
	}

	private void abortStages(final Set<AbstractStage> currentTreadableStages) {
		synchronized (currentTreadableStages) {
			for (AbstractStage stage : currentTreadableStages) {
				STAGE_FACADE.abort(stage);
			}
		}
	}

	@Override
	public void onFinish() {
		try {
			numRunningFiniteProducers.await();
		} catch (InterruptedException e) {
			LOGGER.error("Execution has stopped unexpectedly", e);
			for (Thread thread : this.finiteProducerThreads) {
				thread.interrupt();
			}
		}

		if (!this.infiniteProducerThreads.isEmpty()) {
			LOGGER.debug("Interrupting infiniteProducerThreads...");
			for (Thread thread : this.infiniteProducerThreads) {
				thread.interrupt();
			}
			LOGGER.debug("infiniteProducerThreads have been terminated");
		}

		try {
			numRunningConsumers.await();
		} catch (InterruptedException e) {
			LOGGER.error("Execution has stopped unexpectedly", e);
			synchronized (consumerThreads) {
				for (Thread thread : this.consumerThreads) {
					thread.interrupt();
				}
			}
		}

		// List<Exception> exceptions = collectExceptions();
		// if (!exceptions.isEmpty()) {
		// throw new ExecutionException(exceptions);
		// }
	}

	@Override
	public void onBeforeStart(final AbstractStage stage) {
		switch (STAGE_FACADE.getTerminationStrategy(stage)) {
		case BY_SELF_DECISION:
			numRunningFiniteProducers.countUp();
			break;
		case BY_SIGNAL:
			numRunningConsumers.countUp();
			break;
		default:
			break;
		}
	}

	@Override
	public void onAfterTermination(final AbstractStage stage) {
		switch (STAGE_FACADE.getTerminationStrategy(stage)) {
		case BY_SELF_DECISION:
			numRunningFiniteProducers.countDown();
			break;
		case BY_SIGNAL:
			numRunningConsumers.countDown();
			break;
		default:
			break;
		}
	}

	// TODO impl throw exception... see line 175
	// private List<Exception> collectExceptions() {
	// // Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();
	// List<Exception> exceptions = new ArrayList<Exception>();
	//
	// // for (Stage stage : threadableStages.keySet()) {
	// // List<Exception> stageExceptions = stage.exceptionListener.getExceptions();
	// // exceptions.addAll(stageExceptions);
	// // }
	//
	// return exceptions;
	// }

	// @Override
	// void merge(final ThreadService source) {
	// threadableStages.putAll(source.getThreadableStages());
	// // runnableCounter.inc(source.runnableCounter);
	// }

}
