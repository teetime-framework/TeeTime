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
package teetime.framework.exceptionHandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Stage;

/**
 * Represent a minimalistic StageExceptionListener. Listener which extend from this one, must a least implement this functionality.
 * This abstract class provides a Logger {@link #logger} and a method to terminate the threads execution {@link #terminateExecution()}.
 */
public abstract class StageExceptionHandler {

	public enum FurtherExecution {
		CONTINUE, TERMINATE
	}

	/**
	 * The default logger, which can be used by all subclasses
	 */
	protected final Logger logger;

	public StageExceptionHandler() {
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
	}

	/**
	 * This method will be executed if an exception arises.
	 *
	 * @param e
	 *            thrown exception
	 * @param throwingStage
	 *            the stage, which has thrown the exception.
	 * @return
	 *         true, if the thread should be terminated, false otherwise
	 */
	public abstract FurtherExecution onStageException(Exception e, Stage throwingStage);

}
