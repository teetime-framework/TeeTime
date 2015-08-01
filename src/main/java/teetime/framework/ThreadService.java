package teetime.framework;

import java.util.ArrayList;
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

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final SignalingCounter runnableCounter = new SignalingCounter();
	private final Configuration configuration;

	private Set<Stage> threadableStages;

	public ThreadService(final Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	void onInitialize() {
		Stage startStage = configuration.getStartStage();
		if (startStage == null) {
			throw new IllegalStateException("The start stage may not be null.");
		}

		// TODO visit(port) only
		// TODO use decorator pattern to combine all analyzes so that only one traverser pass is necessary
		IPortVisitor portVisitor = new A0UnconnectedPort();
		IPipeVisitor pipeVisitor = new A1PipeInstantiation();
		Traverser traversor = new Traverser(portVisitor, pipeVisitor, Direction.BOTH);
		traversor.traverse(startStage);

		A2ThreadableStageCollector stageCollector = new A2ThreadableStageCollector();
		traversor = new Traverser(stageCollector, Direction.BOTH);
		traversor.traverse(startStage);

		threadableStages = stageCollector.getThreadableStages();
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}

		A3InvalidThreadAssignmentCheck checker = new A3InvalidThreadAssignmentCheck(threadableStages);
		checker.check();

		A4StageAttributeSetter attributeSetter = new A4StageAttributeSetter(configuration, threadableStages);
		attributeSetter.setAttributes();

		for (Stage stage : threadableStages) {
			categorizeThreadableStage(stage);
		}

		onStart();
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

	@Override
	void onStart() {
		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);

		sendInitializingSignal();
	}

	@Override
	void onExecute() {
		sendStartingSignal();
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

	private void startThreads(final Iterable<Thread> threads) {
		for (Thread thread : threads) {
			thread.start();
		}
	}

	private void sendInitializingSignal() {
		for (Thread thread : infiniteProducerThreads) {
			((TeeTimeThread) thread).sendInitializingSignal();
		}
		for (Thread thread : finiteProducerThreads) {
			((TeeTimeThread) thread).sendInitializingSignal();
		}
	}

	private void sendStartingSignal() {
		for (Thread thread : infiniteProducerThreads) {
			((TeeTimeThread) thread).sendStartingSignal();
		}
		for (Thread thread : finiteProducerThreads) {
			((TeeTimeThread) thread).sendStartingSignal();
		}
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
