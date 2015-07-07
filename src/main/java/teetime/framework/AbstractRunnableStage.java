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

import teetime.framework.exceptionHandling.StageException;
import teetime.framework.signal.TerminatingSignal;

abstract class AbstractRunnableStage implements Runnable {

	private static final String TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION = "Terminating thread due to the following exception: ";

	protected final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	public AbstractRunnableStage(final Stage stage) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());

		// stage.owningContext.getThreadCounter().inc();
	}

	@Override
	public final void run() {
		this.logger.debug("Executing runnable stage...");
		boolean failed = false;
		try {
			beforeStageExecution();
			try {
				do {
					executeStage();
				} while (!stage.shouldBeTerminated());
			} catch (StageException e) {
				this.stage.terminate();
				failed = true;
			}
			afterStageExecution();

		} catch (RuntimeException e) {
			this.logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
			throw e;
		} catch (InterruptedException e) {
			this.logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
		if (failed) {
			if (stage.getTerminationStrategy() == TerminationStrategy.BY_SIGNAL) {
				TerminatingSignal signal = new TerminatingSignal();
				// TODO: Check if this is really needed... it seems like signals are passed on after their first arrival
				InputPort<?>[] inputPorts = stage.getInputPorts();
				for (int i = 0; i < inputPorts.length; i++) {
					stage.onSignal(signal, inputPorts[i]);
				}
			}
			throw new IllegalStateException("Terminated by StageExceptionListener");
		}

		// normal and exceptional termination
		// stage.owningContext.getThreadCounter().dec();
	}

	protected abstract void beforeStageExecution() throws InterruptedException;

	protected abstract void executeStage();

	protected abstract void afterStageExecution();

}
