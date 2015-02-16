/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.signal.ValidatingSignal;
import teetime.framework.validation.AnalysisNotValidException;
import teetime.util.Pair;

/**
 * Represents an Analysis to which stages can be added and executed later.
 * This needs a {@link AnalysisConfiguration},
 * in which the adding and configuring of stages takes place.
 * To start the analysis {@link #init()} and {@link #start()} need to be executed in this order.
 * This class will automatically create threads and join them without any further commitment.
 */
public class Analysis implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

	private final AnalysisConfiguration configuration;

	private final List<Thread> consumerThreads = new LinkedList<Thread>();
	private final List<Thread> finiteProducerThreads = new LinkedList<Thread>();
	private final List<Thread> infiniteProducerThreads = new LinkedList<Thread>();

	private final Collection<Pair<Thread, Throwable>> exceptions = new ConcurrentLinkedQueue<Pair<Thread, Throwable>>();

	private boolean initialized;

	/**
	 * Creates a new {@link Analysis} that skips validating the port connections.
	 *
	 * @param configuration
	 *            to be used for the analysis
	 */
	public Analysis(final AnalysisConfiguration configuration) {
		this(configuration, false);
	}

	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	// TODO remove @SuppressWarnings if init is no longer deprecated
	public Analysis(final AnalysisConfiguration configuration, final boolean validationEnabled) {
		this.configuration = configuration;
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
	 * This initializes Analysis and needs to be run right before starting it.
	 *
	 * @deprecated 1.1
	 */
	@Deprecated
	public void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		final List<Stage> threadableStageJobs = this.configuration.getThreadableStageJobs();
		for (Stage stage : threadableStageJobs) {
			switch (stage.getTerminationStrategy()) {
			case BY_SIGNAL: {
				RunnableConsumerStage runnable;
				if (stage instanceof AbstractConsumerStage<?>) {
					runnable = new RunnableConsumerStage(stage, ((AbstractConsumerStage<?>) stage).getIdleStrategy()); // FIXME remove this word-around
				} else {
					runnable = new RunnableConsumerStage(stage);
				}
				final Thread thread = new Thread(runnable);
				stage.setOwningThread(thread);
				this.consumerThreads.add(thread);
				break;
			}
			case BY_SELF_DECISION: {
				final Thread thread = new Thread(new RunnableProducerStage(stage));
				stage.setOwningThread(thread);
				this.finiteProducerThreads.add(thread);
				break;
			}
			case BY_INTERRUPT: {
				final Thread thread = new Thread(new RunnableProducerStage(stage));
				stage.setOwningThread(thread);
				this.infiniteProducerThreads.add(thread);
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
	 */
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

	private void startThreads(final Iterable<Thread> threads) {
		for (Thread thread : threads) {
			thread.setUncaughtExceptionHandler(this);
			thread.start();
		}
	}

	/**
	 * Retrieves the Configuration which was used to add and arrange all stages needed for the Analysis
	 *
	 * @return Configuration used for the Analysis
	 */
	public AnalysisConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable throwable) {
		this.exceptions.add(Pair.of(thread, throwable));
	}
}
