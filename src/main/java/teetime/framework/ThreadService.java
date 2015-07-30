package teetime.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.IExceptionListenerFactory;
import teetime.util.ThreadThrowableContainer;
import teetime.util.framework.concurrent.SignalingCounter;

/**
 * A Service which manages thread creation and running.
 *
 * @author Nelson Tavares de Sousa
 *
 * @since 2.0
 */
class ThreadService extends AbstractService<ThreadService> {

	private Map<Stage, String> threadableStages = new HashMap<Stage, String>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadService.class);

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final SignalingCounter runnableCounter = new SignalingCounter();

	private final AbstractCompositeStage compositeStage;

	public ThreadService(final AbstractCompositeStage compositeStage) {
		this.compositeStage = compositeStage;

	}

	private final Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();

	private final List<RunnableProducerStage> producerRunnables = new LinkedList<RunnableProducerStage>();

	@Override
	void onInitialize() {
		IExceptionListenerFactory factory;
		try {
			factory = ((Configuration) compositeStage).getFactory();
		} catch (ClassCastException e) {
			throw new IllegalStateException("Something went wrong");
		}
		if (threadableStages.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}

		for (Stage stage : threadableStages.keySet()) {
			final Thread thread = initializeStage(stage);

			final Set<Stage> intraStages = traverseIntraStages(stage);

			final AbstractExceptionListener newListener = factory.createInstance();
			initializeIntraStages(intraStages, thread, newListener);
		}

		onStart();
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
		for (Stage stage : threadableStages.keySet()) {
			stage.terminate();
		}
	}

	@Override
	void onFinish() {
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

		// if (!exceptions.isEmpty()) {
		// throw new ExecutionException(exceptions);
		// }
	}

	private void initializeIntraStages(final Set<Stage> intraStages, final Thread thread, final AbstractExceptionListener newListener) {
		for (Stage intraStage : intraStages) {
			intraStage.setOwningThread(thread);
			intraStage.setExceptionHandler(newListener);
		}
	}

	private Thread initializeStage(final Stage stage) {
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
			break;
		}
		case BY_INTERRUPT: {
			final RunnableProducerStage runnable = new RunnableProducerStage(stage);
			producerRunnables.add(runnable);
			thread = createThread(runnable, stage.getId());
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

	private Set<Stage> traverseIntraStages(final Stage stage) {
		final Traversor traversor = new Traversor(new IntraStageCollector());
		traversor.traverse(stage);
		return traversor.getVisitedStage();
	}

	void addThreadableStage(final Stage stage, final String threadName) {
		if (this.threadableStages.put(stage, threadName) != null && LOGGER.isWarnEnabled()) {
			LOGGER.warn("Stage " + stage.getId() + " was already marked as threadable stage.");
		}
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
	void merge(final ThreadService source) {
		this.getThreadableStages().putAll(source.getThreadableStages());
	}

	SignalingCounter getRunnableCounter() {
		return runnableCounter;
	}

}
