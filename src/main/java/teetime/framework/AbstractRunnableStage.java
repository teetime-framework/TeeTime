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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.TerminateException;

abstract class AbstractRunnableStage implements Runnable {

	private static final String TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION = "Terminating thread due to the following exception: ";

	protected final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	protected AbstractRunnableStage(final Stage stage) {
		if (stage == null) {
			throw new IllegalArgumentException("Argument stage may not be null");
		}

		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public final void run() {
		final Stage stage = this.stage; // should prevent the stage to be reloaded after a volatile read
		this.logger.debug("Executing runnable stage...");

		try {
			try {
				beforeStageExecution();
				try {
					do {
						executeStage();
					} while (!Thread.currentThread().isInterrupted());
				} catch (TerminateException e) {
					this.stage.terminate();
					stage.getOwningContext().abortConfigurationRun();
				} finally {
					afterStageExecution();
				}

			} catch (RuntimeException e) {
				this.logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
				throw e;
			} catch (InterruptedException e) {
				this.logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
			}
		} finally {
			if (stage.getTerminationStrategy() != TerminationStrategy.BY_INTERRUPT) {
				stage.getOwningContext().getThreadService().getRunnableCounter().dec();
			}
		}

		logger.debug("Finished runnable stage. (" + stage.getId() + ")");
	}

	protected abstract void beforeStageExecution() throws InterruptedException;

	protected abstract void executeStage();

	protected abstract void afterStageExecution();

	public static AbstractRunnableStage create(final Stage stage) {
		if (stage.getTerminationStrategy() == TerminationStrategy.BY_SIGNAL) {
			return new RunnableConsumerStage(stage);
		} else {
			return new RunnableProducerStage(stage);
		}
	}

}
