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

import java.util.ArrayList;
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
class ThreadService extends AbstractService<ThreadService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadService.class);

	private final List<Thread> consumerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> finiteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());
	private final List<Thread> infiniteProducerThreads = Collections.synchronizedList(new LinkedList<Thread>());

	private final SignalingCounter runnableCounter = new SignalingCounter();
	private final Set<Stage> threadableStages = Collections.synchronizedSet(new HashSet<Stage>());

	private final Configuration configuration;

	public ThreadService(final Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	void onInitialize() {
		Stage startStage = configuration.getStartStage();

		Set<Stage> newThreadableStages = initialize(startStage);
		startThreads(newThreadableStages);
		sendInitializingSignal(newThreadableStages);
	}

	void startStageAtRuntime(final Stage newStage) {
		configuration.addThreadableStage(newStage);

		Set<Stage> newThreadableStages = initialize(newStage);
		startThreads(newThreadableStages);
		sendInitializingSignal(newThreadableStages);

		sendStartingSignal(newThreadableStages);
	}

	// extracted for runtime use
	private Set<Stage> initialize(final Stage startStage) {
		if (startStage == null) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		// TODO use decorator pattern to combine all analyzes so that only one traverser pass is necessary

		A1ThreadableStageCollector stageCollector = new A1ThreadableStageCollector();
		Traverser traversor = new Traverser(stageCollector, Direction.BOTH);
		traversor.traverse(startStage);

		Set<Stage> newThreadableStages = stageCollector.getThreadableStages();

		threadableStages.addAll(newThreadableStages);
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}

		A2InvalidThreadAssignmentCheck checker = new A2InvalidThreadAssignmentCheck(newThreadableStages);
		checker.check();

		A3PipeInstantiation pipeVisitor = new A3PipeInstantiation();
		traversor = new Traverser(pipeVisitor, Direction.BOTH);
		traversor.traverse(startStage);

		A4StageAttributeSetter attributeSetter = new A4StageAttributeSetter(configuration, newThreadableStages);
		attributeSetter.setAttributes();

		for (Stage stage : newThreadableStages) {
			categorizeThreadableStage(stage);
		}

		return newThreadableStages;
	}

	private void categorizeThreadableStage(final Stage stage) {
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
			LOGGER.warn("Unknown termination strategy '" + stage.getTerminationStrategy() + "' in stage " + stage);// NOPMD
			break;
		}
	}

	private void startThreads(final Set<Stage> threadableStages) {
		for (Stage stage : threadableStages) {
			stage.getOwningThread().start();
		}
	}

	private void sendInitializingSignal(final Set<Stage> threadableStages) {
		for (Stage stage : threadableStages) {
			((TeeTimeThread) stage.getOwningThread()).sendInitializingSignal();
		}
	}

	private void sendStartingSignal(final Set<Stage> newThreadableStages) {
		for (Stage stage : newThreadableStages) {
			((TeeTimeThread) stage.getOwningThread()).sendStartingSignal();
		}
	}

	@Override
	void onExecute() {
		sendStartingSignal(threadableStages);
	}

	@Override
	void onTerminate() {
		for (Stage stage : threadableStages) {
			stage.terminate();
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

		List<Exception> exceptions = collectExceptions();
		if (!exceptions.isEmpty()) {
			// throw new ExecutionException(exceptions);
		}
	}

	// TODO impl throw exception
	private List<Exception> collectExceptions() {
		// Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();
		List<Exception> exceptions = new ArrayList<Exception>();

		// for (Stage stage : threadableStages.keySet()) {
		// List<Exception> stageExceptions = stage.exceptionListener.getExceptions();
		// exceptions.addAll(stageExceptions);
		// }

		return exceptions;
	}

	Set<Stage> getThreadableStages() {
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
