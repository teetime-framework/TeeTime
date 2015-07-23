package teetime.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.InitializingSignal;
import teetime.util.ThreadThrowableContainer;
import teetime.util.framework.concurrent.SignalingCounter;

/**
 * A Service which manages thread creation and running.
 *
 * @author Nelson Tavares de Sousa
 *
 */
class ThreadService extends AbstractService<ThreadService> {

	private Map<Stage, String> threadableStages = new HashMap<Stage, String>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadService.class);

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final SignalingCounter runnableCounter = new SignalingCounter();

	SignalingCounter getRunnableCounter() {
		return runnableCounter;
	}

	private final Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();

	private final List<RunnableProducerStage> producerRunnables = new LinkedList<RunnableProducerStage>();

	Thread initializeThreadableStages(final Stage stage) {
		final Thread thread;

		final TerminationStrategy terminationStrategy = stage.getTerminationStrategy();
		switch (terminationStrategy) {
		case BY_SIGNAL: {
			final RunnableConsumerStage runnable = new RunnableConsumerStage(stage);
			thread = createThread(runnable, stage.getId());
			this.consumerThreads.add(thread);
			break;
		}
		case BY_SELF_DECISION: {
			final RunnableProducerStage runnable = new RunnableProducerStage(stage);
			producerRunnables.add(runnable);
			thread = createThread(runnable, stage.getId());
			this.finiteProducerThreads.add(thread);
			InitializingSignal initializingSignal = new InitializingSignal();
			stage.onSignal(initializingSignal, null);
			break;
		}
		case BY_INTERRUPT: {
			final RunnableProducerStage runnable = new RunnableProducerStage(stage);
			producerRunnables.add(runnable);
			thread = createThread(runnable, stage.getId());
			InitializingSignal initializingSignal = new InitializingSignal();
			stage.onSignal(initializingSignal, null);
			this.infiniteProducerThreads.add(thread);
			break;
		}
		default:
			throw new IllegalStateException("Unhandled termination strategy: " + terminationStrategy);
		}
		return thread;
	}

	private Thread createThread(final AbstractRunnableStage runnable, final String name) {
		final Thread thread = new Thread(runnable);
		thread.setName(threadableStages.get(runnable.stage));
		return thread;
	}

	void addThreadableStage(final Stage stage, final String threadName) {
		if (this.threadableStages.put(stage, threadName) != null) {
			LOGGER.warn("Stage " + stage.getId() + " was already marked as threadable stage.");
		}
	}

	void waitForTermination() {
		try {
			runnableCounter.waitFor(0);

			// LOGGER.debug("Waiting for finiteProducerThreads");
			// for (Thread thread : this.finiteProducerThreads) {
			// thread.join();
			// }
			//
			// LOGGER.debug("Waiting for consumerThreads");
			// for (Thread thread : this.consumerThreads) {
			// thread.join();
			// }
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

		if (!exceptions.isEmpty()) {
			throw new ExecutionException(exceptions);
		}
	}

	void executeNonBlocking() {
		sendStartingSignal();
	}

	void startThreads() {
		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);

		sendInitializingSignal();
	}

	private void startThreads(final Iterable<Thread> threads) {
		for (Thread thread : threads) {
			thread.start();
		}
	}

	private void sendInitializingSignal() {
		for (RunnableProducerStage runnable : producerRunnables) {
			runnable.triggerInitializingSignal();
		}
	}

	private void sendStartingSignal() {
		for (RunnableProducerStage runnable : producerRunnables) {
			runnable.triggerStartingSignal();
		}
	}

	Map<Stage, String> getThreadableStages() {
		return threadableStages;
	}

	void setThreadableStages(final Map<Stage, String> threadableStages) {
		this.threadableStages = threadableStages;
	}

	@Override
	void merge(final ThreadService target, final ThreadService source) {
		// TODO Auto-generated method stub

	}

}
