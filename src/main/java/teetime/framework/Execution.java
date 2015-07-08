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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.IExceptionListenerFactory;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.util.ThreadThrowableContainer;

/**
 * Represents an Execution to which stages can be added and executed later.
 * This needs a {@link Configuration},
 * in which the adding and configuring of stages takes place.
 * To start the analysis {@link #executeBlocking()} needs to be executed.
 * This class will automatically create threads and join them without any further commitment.
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @param <T>
 *            the type of the {@link Configuration}
 *
 * @since 2.0
 */
public final class Execution<T extends Configuration> implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Execution.class);

	private final T configuration;

	private final IExceptionListenerFactory factory;

	private boolean executionInterrupted = false;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final Collection<ThreadThrowableContainer> exceptions = new ConcurrentLinkedQueue<ThreadThrowableContainer>();

	private final List<RunnableProducerStage> producerRunnables = new LinkedList<RunnableProducerStage>();

	/**
	 * Creates a new {@link Execution} that skips validating the port connections and uses the default listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 */
	public Execution(final T configuration) {
		this(configuration, false);
	}

	/**
	 * Creates a new {@link Execution} that uses the default listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 * @param validationEnabled
	 *            whether or not the validation should be executed
	 */
	public Execution(final T configuration, final boolean validationEnabled) {
		this(configuration, validationEnabled, new TerminatingExceptionListenerFactory());
	}

	/**
	 * Creates a new {@link Execution} that skips validating the port connections and uses a specific listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 * @param factory
	 *            specific listener for the exception handling
	 */
	public Execution(final T configuration, final IExceptionListenerFactory factory) {
		this(configuration, false, factory);
	}

	/**
	 * Creates a new {@link Execution} that uses a specific listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 * @param validationEnabled
	 *            whether or not the validation should be executed
	 * @param factory
	 *            specific listener for the exception handling
	 */
	public Execution(final T configuration, final boolean validationEnabled, final IExceptionListenerFactory factory) {
		this.configuration = configuration;
		this.factory = factory;
		if (validationEnabled) {
			validateStages();
		}
		init();
	}

	// BETTER validate concurrently
	private void validateStages() {
		final Map<Stage, String> threadableStageJobs = this.configuration.getContext().getThreadableStages();
		for (Stage stage : threadableStageJobs.keySet()) {
			// // portConnectionValidator.validate(stage);
			// }

			final ValidatingSignal validatingSignal = new ValidatingSignal();
			stage.onSignal(validatingSignal, null);
			if (validatingSignal.getInvalidPortConnections().size() > 0) {
				throw new AnalysisNotValidException(validatingSignal.getInvalidPortConnections());
			}
		}
	}

	/**
	 * This initializes the analysis and needs to be run right before starting it.
	 *
	 */
	private final void init() {
		ExecutionInstantiation executionInstantiation = new ExecutionInstantiation(configuration.getContext());
		executionInstantiation.instantiatePipes();

		final Set<Stage> threadableStageJobs = this.configuration.getContext().getThreadableStages().keySet();
		if (threadableStageJobs.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}

		for (Stage stage : threadableStageJobs) {
			final Thread thread = initializeThreadableStages(stage);

			final Set<Stage> intraStages = traverseIntraStages(stage);
			final AbstractExceptionListener newListener = factory.createInstance();
			initializeIntraStages(intraStages, thread, newListener);
		}

		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);

		sendInitializingSignal();

	}

	private Thread initializeThreadableStages(final Stage stage) {
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
		thread.setUncaughtExceptionHandler(this);
		thread.setName(configuration.getContext().getThreadableStages().get(runnable.stage));
		return thread;
	}

	private void initializeIntraStages(final Set<Stage> intraStages, final Thread thread, final AbstractExceptionListener newListener) {
		for (Stage intraStage : intraStages) {
			intraStage.setOwningThread(thread);
			intraStage.setExceptionHandler(newListener);
		}
	}

	/**
	 * Calling this method will block the current thread, until the analysis terminates.
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the analysis. The exception contains the pairs of thread and throwable
	 *
	 * @since 2.0
	 */
	public void waitForTermination() {
		try {
			// stage.owningContext.getThreadCounter().await(0);

			LOGGER.debug("Waiting for finiteProducerThreads");
			for (Thread thread : this.finiteProducerThreads) {
				thread.join();
			}

			LOGGER.debug("Waiting for consumerThreads");
			for (Thread thread : this.consumerThreads) {
				thread.join();
			}
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

	// TODO: implement
	private void abortEventually() {
		for (Thread thread : this.finiteProducerThreads) {
			thread.interrupt();
		}

		for (Thread thread : this.consumerThreads) {
			thread.interrupt();
		}

		for (Thread thread : this.infiniteProducerThreads) {
			thread.interrupt();
		}
	}

	/**
	 * This method will start the Execution and block until it is finished.
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the analysis. The exception contains the pairs of thread and throwable.
	 *
	 * @since 2.0
	 */
	public void executeBlocking() {
		executeNonBlocking();
		waitForTermination();
	}

	/**
	 * This method starts the analysis without waiting for its termination. The method {@link #waitForTermination()} must be called to unsure a correct termination
	 * of the analysis.
	 *
	 * @since 2.0
	 */
	public void executeNonBlocking() {
		sendStartingSignal();
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

	/**
	 * Retrieves the Configuration which was used to add and arrange all stages needed for the Execution
	 *
	 * @return the configuration used for the Execution
	 */
	public T getConfiguration() {
		return this.configuration;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		if (!executionInterrupted) {
			executionInterrupted = true;
			LOGGER.warn("Thread " + thread + " was interrupted. Terminating analysis now.");
			for (Stage stage : configuration.getContext().getThreadableStages().keySet()) {
				if (stage.getOwningThread() != thread) {
					if (stage.getTerminationStrategy() == TerminationStrategy.BY_SELF_DECISION) {
						stage.terminate();
					}
				}
			}
		}
		this.exceptions.add(ThreadThrowableContainer.of(thread, throwable));
	}

	private Set<Stage> traverseIntraStages(final Stage stage) {
		final Traversor traversor = new Traversor(new IntraStageCollector());
		traversor.traverse(stage);
		return traversor.getVisitedStage();
	}

	/**
	 * Returns the specified ExceptionListenerFactory
	 *
	 * @return
	 *         a given ExceptionListenerFactory instance
	 * 
	 * @since 2.0
	 */
	public IExceptionListenerFactory getFactory() {
		return factory;
	}
}
