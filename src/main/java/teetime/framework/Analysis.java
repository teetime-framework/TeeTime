/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
import teetime.framework.exceptionHandling.IgnoringExceptionListenerFactory;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.pipe.UnboundedSpScPipeFactory;
import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.util.Connection;
import teetime.util.Pair;

/**
 * Represents an Analysis to which stages can be added and executed later.
 * This needs a {@link AnalysisConfiguration},
 * in which the adding and configuring of stages takes place.
 * To start the analysis {@link #executeBlocking()} needs to be executed.
 * This class will automatically create threads and join them without any further commitment.
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @param <T>
 *            the type of the {@link AnalysisConfiguration}
 */
public final class Analysis<T extends AnalysisConfiguration> implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

	private final T configuration;

	private final IExceptionListenerFactory factory;

	private boolean executionInterrupted = false;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final Collection<Pair<Thread, Throwable>> exceptions = new ConcurrentLinkedQueue<Pair<Thread, Throwable>>();

	private boolean initialized;

	private final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();
	private Integer connected = new Integer(0);

	/**
	 * Creates a new {@link Analysis} that skips validating the port connections and uses the default listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 */
	public Analysis(final T configuration) {
		this(configuration, false, new IgnoringExceptionListenerFactory());
	}

	public Analysis(final T configuration, final boolean validationEnabled) {
		this(configuration, validationEnabled, new IgnoringExceptionListenerFactory());
	}

	/**
	 * Creates a new {@link Analysis} that skips validating the port connections and uses a specific listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 * @param factory
	 *            specific listener for the exception handling
	 */
	public Analysis(final T configuration, final IExceptionListenerFactory factory) {
		this(configuration, false, factory);
	}

	public Analysis(final T configuration, final boolean validationEnabled, final IExceptionListenerFactory factory) {
		this.configuration = configuration;
		this.factory = factory;
		if (validationEnabled) {
			validateStages();
		}
		init();
	}

	// BETTER validate concurrently
	private void validateStages() {
		final Set<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
		for (Stage stage : threadableStageJobs) {
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
		if (initialized) {
			return;
		}
		initialized = true;

		prototypeInstantiatePipes();

		final Set<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
		if (threadableStageJobs.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}

		for (Stage stage : threadableStageJobs) {
			final Thread thread = initializeStages(stage);

			final Set<Stage> intraStages = traverseIntraStages(stage);
			final AbstractExceptionListener newListener = factory.createInstance();
			initializeIntraStages(intraStages, thread, newListener);
		}

	}

	private Thread initializeStages(final Stage stage) {
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
			thread = createThread(runnable, stage.getId());
			this.finiteProducerThreads.add(thread);
			InitializingSignal initializingSignal = new InitializingSignal();
			stage.onSignal(initializingSignal, null);
			break;
		}
		case BY_INTERRUPT: {
			final RunnableProducerStage runnable = new RunnableProducerStage(stage);
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

	private void instantiatePipes() {
		Set<Stage> threadableStageJobs = configuration.getThreadableStageJobs();
		for (Connection connection : configuration.getConnections()) {
			if (threadableStageJobs.contains(connection.getTargetPort().getOwningStage())) {
				if (connection.getCapacity() != 0) {
					interBoundedThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort(), connection.getCapacity());
				} else {
					interUnboundedThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort(), 4);
				}
			} else {
				intraThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort());
			}
		}
	}

	private void prototypeInstantiatePipes() {
		Integer i = new Integer(0);
		Map<Stage, Integer> colors = new HashMap<Stage, Integer>();
		Set<Stage> threadableStageJobs = configuration.getThreadableStageJobs();
		for (Stage threadableStage : threadableStageJobs) {
			i++;
			colors.put(threadableStage, i); // Markiere den threadHead
			colorAndConnectStages(i, colors, threadableStage);
		}
		if (configuration.getConnections().size() != connected) {
			throw new IllegalStateException("remaining " + (configuration.getConnections().size() - connected) + " connections");
		}
	}

	public void colorAndConnectStages(final Integer i, final Map<Stage, Integer> colors, final Stage threadableStage) {
		Set<Stage> threadableStageJobs = configuration.getThreadableStageJobs();
		for (Connection connection : configuration.getConnections()) {
			// Die Connection gehört zu der Stage
			if (connection.getSourcePort().getOwningStage() == threadableStage) {
				Stage targetStage = connection.getTargetPort().getOwningStage();
				Integer targetColor = new Integer(0);
				if (colors.containsKey(targetStage)) {
					targetColor = colors.get(targetStage);
				}
				if (threadableStageJobs.contains(targetStage) && targetColor.compareTo(i) != 0) { // Auch auf Farbe prüfen
					if (connection.getCapacity() != 0) {
						interBoundedThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort(), connection.getCapacity());
					} else {
						interUnboundedThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort(), 4);
					}
				} else {
					if (colors.containsKey(targetStage)) {
						if (colors.get(targetStage).equals(i)) {
							throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (not the "headstage")
						}
					}
					intraThreadPipeFactory.create(connection.getSourcePort(), connection.getTargetPort());
					colors.put(targetStage, i);
					colorAndConnectStages(i, colors, targetStage);
				}
				connected++;
				// configuration.getConnections().remove(connection); remove connection to increase performance
			}
		}
	}

	private Thread createThread(final AbstractRunnableStage runnable, final String name) {
		final Thread thread = new Thread(runnable);
		thread.setUncaughtExceptionHandler(this);
		thread.setName(name);
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
	 * @throws AnalysisException
	 *             if at least one exception in one thread has occurred within the analysis. The exception contains the pairs of thread and throwable
	 *
	 * @since 1.1
	 */
	public void waitForTermination() {
		try {
			for (Thread thread : this.finiteProducerThreads) {
				thread.join();
			}

			for (Thread thread : this.consumerThreads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			LOGGER.error("Analysis has stopped unexpectedly", e);
			for (Thread thread : this.finiteProducerThreads) {
				thread.interrupt();
			}

			for (Thread thread : this.consumerThreads) {
				thread.interrupt();
			}
		}

		for (Thread thread : this.infiniteProducerThreads) {
			thread.interrupt();
		}

		if (!exceptions.isEmpty()) {
			throw new AnalysisException(exceptions);
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
	 * This method will start the Analysis and block until it is finished.
	 *
	 * @throws AnalysisException
	 *             if at least one exception in one thread has occurred within the analysis. The exception contains the pairs of thread and throwable.
	 *
	 * @since 1.1
	 */
	public void executeBlocking() {
		executeNonBlocking();
		waitForTermination();
	}

	/**
	 * This method starts the analysis without waiting for its termination. The method {@link #waitForTermination()} must be called to unsure a correct termination
	 * of the analysis.
	 *
	 * @since 1.1
	 */
	public void executeNonBlocking() {
		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);
	}

	private void startThreads(final Iterable<Thread> threads) {
		for (Thread thread : threads) {
			thread.start();
		}
	}

	/**
	 * Retrieves the Configuration which was used to add and arrange all stages needed for the Analysis
	 *
	 * @return the configuration used for the Analysis
	 */
	public T getConfiguration() {
		return this.configuration;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		if (!executionInterrupted) {
			executionInterrupted = true;
			LOGGER.warn("Thread " + thread + " was interrupted. Terminating analysis now.");
			for (Stage stage : configuration.getThreadableStageJobs()) {
				if (stage.getOwningThread() != thread) {
					if (stage.getTerminationStrategy() == TerminationStrategy.BY_SELF_DECISION) {
						stage.terminate();
					}
				}
			}
		}
		this.exceptions.add(Pair.of(thread, throwable));
	}

	private Set<Stage> traverseIntraStages(final Stage stage) {
		final Traversor traversor = new Traversor(new IntraStageCollector());
		traversor.traverse(stage);
		return traversor.getVisitedStage();
	}
}
