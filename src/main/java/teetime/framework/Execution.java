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
package teetime.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.scheduling.pushpullmodel.PushPullScheduling;

/**
 * Represents an Execution to which stages can be added and executed later.
 * This class requires a {@link Configuration},
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
public final class Execution<T extends Configuration> {

	private static enum ExecutionState {
		INITIALIZED, CANCELING, CANCELED, EXECUTING, COMPLETED,
	}

	private static class ExecutionFuture implements Future<Void> {

		private final Execution<?> execution;

		public ExecutionFuture(final Execution<?> execution) {
			this.execution = execution;
		}

		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			if (execution.getState() == ExecutionState.COMPLETED) {
				return false;
			}
			if (execution.getState() == ExecutionState.CANCELING) {
				return false;
			}
			if (execution.getState() == ExecutionState.CANCELED) {
				return false;
			}
			if (execution.getState() == ExecutionState.INITIALIZED) {
				return true;
			}
			execution.abortEventually();
			return true;
		}

		@Override
		public boolean isCancelled() {
			return execution.getState() == ExecutionState.CANCELED;
		}

		@Override
		public boolean isDone() {
			return execution.getState() == ExecutionState.COMPLETED;
		}

		@Override
		public Void get() throws InterruptedException, java.util.concurrent.ExecutionException {
			execution.waitForTermination();
			return null;
		}

		@Override
		public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, java.util.concurrent.ExecutionException, TimeoutException {
			throw new UnsupportedOperationException();
		}

	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Execution.class);

	private final T configuration;

	private ExecutionState state;

	private TeeTimeService scheduler;

	/**
	 * Creates a new {@link Execution}. It validates the given configuration and uses the {@link PushPullScheduling} as default scheduler.
	 *
	 * @param configuration
	 *            to be executed
	 */
	public Execution(final T configuration) {
		this(configuration, true);
	}

	/**
	 * Uses {@link PushPullScheduling} as default scheduler.
	 *
	 * @param configuration
	 *            to be executed.
	 * @param validationEnabled
	 *            <code>true</code> if validation should be performed after initialization; <code>false</code> otherwise.
	 */
	public Execution(final T configuration, final boolean validationEnabled) {
		this(configuration, validationEnabled, new PushPullScheduling(configuration));
	}

	/**
	 * @param configuration
	 *            to be executed.
	 * @param validationEnabled
	 *            <code>true</code> if validation should be performed after initialization; <code>false</code> otherwise.
	 * @param scheduler
	 *            to be used for the given configuration.
	 */
	public Execution(final T configuration, final boolean validationEnabled, final TeeTimeService scheduler) {
		this.scheduler = scheduler;
		this.configuration = configuration;
		if (configuration.isInitialized()) {
			throw new IllegalStateException("3001 - Configuration has already been used.");
		}
		configuration.setInitialized(true);

		scheduler.onInitialize();
		state = ExecutionState.INITIALIZED;

		LOGGER.info("Using scheduler: {}", scheduler);

		if (validationEnabled) {
			scheduler.onValidate();
		}
	}

	/**
	 * Calling this method will block the current thread until the execution terminates.
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the execution. The exception contains the pairs of thread and throwable
	 *
	 * @since 2.0
	 */
	public void waitForTermination() {
		scheduler.onFinish();
		if (state == ExecutionState.CANCELING) {
			state = ExecutionState.CANCELED;
		} else {
			state = ExecutionState.COMPLETED;
		}

		Map<Thread, List<Exception>> threadExceptionsMap = configuration.getFactory().getThreadExceptionsMap();
		Iterator<Entry<Thread, List<Exception>>> iterator = threadExceptionsMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Thread, List<Exception>> entry = iterator.next();
			if (entry.getValue().isEmpty()) {
				iterator.remove();
			}
		}

		if (!threadExceptionsMap.isEmpty()) {
			throw new ExecutionException(threadExceptionsMap);
		}
	}

	/**
	 * Terminates all producer stages, interrupts all threads, and waits for a graceful termination.
	 */
	public void abortEventually() {
		state = ExecutionState.CANCELING;
		scheduler.onTerminate();
		waitForTermination();
	}

	/**
	 * This method starts this execution without waiting for its termination. The method {@link #waitForTermination()} must be called to unsure a correct termination
	 * of the execution.
	 *
	 * @return a future object to cancel the execution or to wait for the execution to finish.
	 *
	 * @since 2.0
	 */
	public Future<Void> executeNonBlocking() {
		if (configuration.isExecuted()) {
			throw new IllegalStateException("3002 - Any configuration instance may only be executed once.");
		}
		configuration.setExecuted(true);
		state = ExecutionState.EXECUTING;
		scheduler.onExecute();
		return new ExecutionFuture(this);
	}

	/**
	 * This method will start this execution and block until it is finished.
	 *
	 * @throws ExecutionException
	 *             if at least one exception in one thread has occurred within the execution. The exception contains the pairs of thread and throwable.
	 *
	 * @since 2.0
	 */
	public void executeBlocking() {
		executeNonBlocking();
		waitForTermination();
	}

	/**
	 * Retrieves the Configuration which was used to add and arrange all stages needed for this execution.
	 *
	 * @return the configuration used for this execution
	 */
	public T getConfiguration() {
		return this.configuration;
	}

	private static List<Configuration> configLoader(final String... args) {
		List<Configuration> instances = new ArrayList<Configuration>();
		for (String each : args) {
			try {
				Class<?> clazz = Class.forName(each);
				Object obj = clazz.newInstance();
				if (obj instanceof Configuration) {
					instances.add((Configuration) obj);
				}
			} catch (ClassNotFoundException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Could not find class " + each);
				}
			} catch (InstantiationException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Could not instantiate class " + each, e);
				}
			} catch (IllegalAccessException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("IllegalAccessException arised while instantiating class " + each, e);
				}
			}
		}
		return instances;
	}

	public static void main(final String... args) {
		List<Configuration> instances = configLoader(args);
		for (Configuration configuration : instances) {
			new Execution<Configuration>(configuration).executeBlocking(); // NOPMD
		}
	}

	/* default */ ExecutionState getState() {
		return state;
	}

}
