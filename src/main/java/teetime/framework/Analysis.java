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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.IExceptionListenerFactory;
import teetime.framework.exceptionHandling.IgnoringExceptionListenerFactory;
import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.util.Pair;

/**
 * Represents an Analysis to which stages can be added and executed later.
 * This needs a {@link AnalysisConfiguration},
 * in which the adding and configuring of stages takes place.
 * To start the analysis {@link #executeBlocking()} needs to be executed.
 * This class will automatically create threads and join them without any further commitment.
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

	/**
	 * Creates a new {@link Analysis} that skips validating the port connections and uses the default listener.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 */
	public Analysis(final T configuration) {
		this(configuration, false, new IgnoringExceptionListenerFactory());
	}

	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	// TODO remove @SuppressWarnings if init is no longer deprecated
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

	private void validateStages() {
		// BETTER validate concurrently
		final List<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
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
	 * @deprecated since 1.1, analysis will be initialized automatically by the framework
	 */
	@Deprecated
	public final void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		final List<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
		if (threadableStageJobs.isEmpty()) {
			throw new IllegalStateException("No stage was added using the addThreadableStage(..) method. Add at least one stage.");
		}
		AbstractExceptionListener newListener;
		Set<Stage> intraStages;
		for (Stage stage : threadableStageJobs) {
			intraStages = traverseIntraStages(stage);
			newListener = factory.create();
			switch (stage.getTerminationStrategy()) {
			case BY_SIGNAL: {
				final RunnableConsumerStage runnableConsumerStage = new RunnableConsumerStage(stage, newListener);
				runnableConsumerStage.setIntraStages(intraStages);
				final Thread thread = new Thread(runnableConsumerStage);
				stage.setOwningThread(thread);
				this.consumerThreads.add(thread);
				thread.setName(stage.getId());
				break;
			}
			case BY_SELF_DECISION: {
				final RunnableProducerStage runnable = new RunnableProducerStage(stage, newListener);
				runnable.setIntraStages(intraStages);
				final Thread thread = new Thread(runnable);
				stage.setOwningThread(thread);
				this.finiteProducerThreads.add(thread);
				thread.setName(stage.getId());
				break;
			}
			case BY_INTERRUPT: {
				final RunnableProducerStage runnable = new RunnableProducerStage(stage, newListener);
				runnable.setIntraStages(intraStages);
				final Thread thread = new Thread(runnable);
				stage.setOwningThread(thread);
				this.infiniteProducerThreads.add(thread);
				thread.setName(stage.getId());
				break;
			}
			default:
				break;
			}
		}

	}

	/**
	 * This method will start the Analysis and all containing stages.
	 *
	 * @return a collection of thread/throwable pairs
	 *
	 * @deprecated since 1.1, replaced by {@link #executeBlocking()}
	 */
	@Deprecated
	public Collection<Pair<Thread, Throwable>> start() {
		// start analysis
		startThreads(this.consumerThreads);
		startThreads(this.finiteProducerThreads);
		startThreads(this.infiniteProducerThreads);

		// wait for the analysis to complete
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
		return this.exceptions;
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

	// public void abortEventually() {
	// for (Thread thread : this.finiteProducerThreads) {
	// thread.interrupt();
	// }
	//
	// for (Thread thread : this.consumerThreads) {
	// thread.interrupt();
	// }
	//
	// for (Thread thread : this.infiniteProducerThreads) {
	// thread.interrupt();
	// }
	// }

	/**
	 * This method will start the Analysis and block until it is finished.
	 *
	 * @throws AnalysisException
	 *             if at least one exception in one thread has occurred within the analysis. The exception contains the pairs of thread and throwable
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
			thread.setUncaughtExceptionHandler(this);
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
		final Traversor traversor = new Traversor(new IntraStageVisitor());
		if (stage.getOutputPorts().length == 0) {
			return new HashSet<Stage>();
		}
		traversor.traverse(stage, stage.getOutputPorts()[0].getPipe());
		return traversor.getVisitedStage();
	}
}
