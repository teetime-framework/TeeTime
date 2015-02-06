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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class RunnableStage implements Runnable {

	protected final Stage stage;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	public RunnableStage(final Stage stage) {
		this.stage = stage;
		this.logger = LoggerFactory.getLogger(stage.getClass());
	}

	@Override
	public final void run() {
		this.logger.debug("Executing runnable stage...");

		try {
			beforeStageExecution();

			do {
				executeStage();
			} while (!this.stage.shouldBeTerminated());

			afterStageExecution();

		} catch (Error e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		} catch (RuntimeException e) {
			this.logger.error("Terminating thread due to the following exception: ", e);
			throw e;
		}

		this.logger.debug("Finished runnable stage. (" + this.stage.getId() + ")");
	}

	protected abstract void beforeStageExecution();

	protected abstract void executeStage();

	protected abstract void afterStageExecution();
}
