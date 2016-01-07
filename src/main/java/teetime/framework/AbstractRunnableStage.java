/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.TerminateException;
import teetime.util.StopWatch;

abstract class AbstractRunnableStage implements Runnable {

	private static final String TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION = "Terminating thread due to the following exception: ";

	private final StopWatch stopWatch = new StopWatch();

	protected final AbstractStage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	public static final Map<AbstractStage, Long> durationsInNs = Collections.synchronizedMap(new LinkedHashMap<AbstractStage, Long>());

	protected AbstractRunnableStage(final AbstractStage stage) {
		if (stage == null) {
			throw new IllegalArgumentException("Argument stage may not be null");
		}

		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public final void run() {
		final AbstractStage stage = this.stage; // should prevent the stage to be reloaded after a volatile read
		final Logger logger = this.logger; // should prevent the logger to be reloaded after a volatile read

		logger.debug("Executing runnable stage...");

		try {
			try {
				beforeStageExecution();
				if (stage.getOwningContext() == null) {
					throw new IllegalArgumentException("Argument stage may not have a nullable owning context");
				}
				stopWatch.start();
				try {
					while (!stage.shouldBeTerminated()) {
						executeStage();
					}
				} catch (TerminateException e) {
					stage.abort();
					stage.getOwningContext().abortConfigurationRun();
				} finally {
					stopWatch.end();
					durationsInNs.put(stage, stopWatch.getDurationInNs());
					afterStageExecution();
				}

			} catch (RuntimeException e) {
				logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
				throw e;
			} catch (InterruptedException e) {
				logger.error(TERMINATING_THREAD_DUE_TO_THE_FOLLOWING_EXCEPTION, e);
			}
		} finally {
			if (stage.getTerminationStrategy() != TerminationStrategy.BY_INTERRUPT) {
				stage.getOwningContext().getThreadService().getRunnableCounter().dec();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Finished runnable stage. (" + stage.getId() + ")");
		}
	}

	protected abstract void beforeStageExecution() throws InterruptedException;

	protected abstract void executeStage();

	protected abstract void afterStageExecution();

	static AbstractRunnableStage create(final AbstractStage stage) {
		if (stage.getInputPorts().size() > 0) {
			return new RunnableConsumerStage(stage);
		} else {
			return new RunnableProducerStage(stage);
		}
	}

}
