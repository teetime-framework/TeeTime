/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Traverser.Direction;
import teetime.util.framework.concurrent.SignalingCounter;

/**
 * A Service which manages thread creation and running.
 *
 * @author Nelson Tavares de Sousa
 *
 * @since 2.0
 */
class ThreadService extends AbstractService<ThreadService> { // NOPMD

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadService.class);

	private final List<Thread> consumerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> finiteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> infiniteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());

	private final SignalingCounter runnableCounter = new SignalingCounter();
	private final Set<AbstractStage> threadableStages = Collections.synchronizedSet(new HashSet<AbstractStage>());

	private final Configuration configuration;

	public ThreadService(final Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	void onInitialize() {
		AbstractStage startStage = configuration.getStartStage();

		Set<AbstractStage> newThreadableStages = initialize(startStage);
		startThreads(newThreadableStages);
	}

	void startStageAtRuntime(final AbstractStage newStage) {
		newStage.declareActive();

		Set<AbstractStage> newThreadableStages = initialize(newStage);
		startThreads(newThreadableStages);

		sendStartingSignal(newThreadableStages);
	}

	// extracted for runtime use
	private Set<AbstractStage> initialize(final AbstractStage startStage) {
		if (startStage == null) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		A1ThreadableStageCollector stageCollector = new A1ThreadableStageCollector();
		Traverser traversor = new Traverser(stageCollector, Direction.BOTH);
		traversor.traverse(startStage);

		Set<AbstractStage> newThreadableStages = stageCollector.getThreadableStages();

		threadableStages.addAll(newThreadableStages);
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("1004 - No threadable stages in this configuration.");
		}

		A2InvalidThreadAssignmentCheck checker = new A2InvalidThreadAssignmentCheck(newThreadableStages);
		checker.check();

		A3PipeInstantiation pipeVisitor = new A3PipeInstantiation();
		traversor = new Traverser(pipeVisitor, Direction.BOTH);
		traversor.traverse(startStage);

		A4StageAttributeSetter attributeSetter = new A4StageAttributeSetter(configuration, newThreadableStages);
		attributeSetter.setAttributes();

		for (AbstractStage stage : newThreadableStages) {
			categorizeThreadableStage(stage);
		}

		return newThreadableStages;
	}

	private void categorizeThreadableStage(final AbstractStage stage) {
		switch (stage.getTerminationStrategy()) {
		case BY_INTERRUPT:
			infiniteProducerThreads.add(stage.getOwningThread());
			break;
		case BY_SELF_DECISION:
			finiteProducerThreads.add(stage.getOwningThread());
			break;
		case BY_SIGNAL:
			consumerThreads.add(stage.getOwningThread());
			break;
		default:
			LOGGER.warn("Unknown termination strategy '{}' in stage {}", stage.getTerminationStrategy(), stage);
			break;
		}
	}

	private void startThreads(final Set<AbstractStage> threadableStages) {
		for (AbstractStage stage : threadableStages) {
			stage.getOwningThread().start();
		}
	}

	private void sendStartingSignal(final Set<AbstractStage> newThreadableStages) {
		synchronized (newThreadableStages) {// TODO why synchronized?
			for (AbstractStage stage : newThreadableStages) {
				((TeeTimeThread) stage.getOwningThread()).sendStartingSignal();
			}
		}
	}

	@Override
	void onExecute() {
		sendStartingSignal(threadableStages);
	}

	@Override
	void onTerminate() {
		abortStages(threadableStages);
	}

	private void abortStages(final Set<AbstractStage> currentTreadableStages) {
		synchronized (currentTreadableStages) {
			for (AbstractStage stage : currentTreadableStages) {
				stage.abort();
			}
		}
	}

	@Override
	void onFinish() {
		try {
			runnableCounter.waitFor(0);
		} catch (InterruptedException e) {
			LOGGER.error("Execution has stopped unexpectedly", e);
			for (Thread thread : this.finiteProducerThreads) {
				thread.interrupt();
			}

			for (Thread thread : this.consumerThreads) {
				thread.interrupt();
			}
		}

		LOGGER.debug("Interrupting infiniteProducerThreads...");
		for (Thread thread : this.infiniteProducerThreads) {
			thread.interrupt();
		}

		// List<Exception> exceptions = collectExceptions();
		// if (!exceptions.isEmpty()) {
		// throw new ExecutionException(exceptions);
		// }
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

	Set<AbstractStage> getThreadableStages() {
		return threadableStages;
	}

	// @Override
	// void merge(final ThreadService source) {
	// threadableStages.putAll(source.getThreadableStages());
	// // runnableCounter.inc(source.runnableCounter);
	// }

	SignalingCounter getRunnableCounter() {
		return runnableCounter;
	}

}
