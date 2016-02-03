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
package teetime.framework.exceptionHandling;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;

/**
 * Represents a minimalistic StageExceptionListener.
 * Listener which extend from this one, must a least implement this functionality.
 * This abstract class provides a Logger {@link #logger} and the method {@link #onStageException(Exception, AbstractStage)} which is called on every raised
 * exception.
 */
public abstract class AbstractExceptionListener {

	private final List<Exception> exceptionsList = new ArrayList<Exception>();
	private final boolean logExceptions;

	public enum FurtherExecution {
		CONTINUE, TERMINATE
	}

	/**
	 * The default logger, which can be used by all subclasses
	 */
	protected final Logger logger; // NOPMD can't be static as it needs to be initialized in cstr

	protected AbstractExceptionListener(final boolean shouldLogExceptions) {
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		this.logExceptions = shouldLogExceptions;
	}

	/**
	 * This method will be executed if an exception arises.
	 *
	 * @param exception
	 *            thrown exception
	 * @param throwingStage
	 *            the stage, which has thrown the exception.
	 * @return
	 * 		true, if the thread should be terminated, false otherwise
	 */
	public abstract FurtherExecution onStageException(Exception exception, AbstractStage throwingStage);

	public List<Exception> getLoggedExceptions() {
		return exceptionsList;
	}

	public FurtherExecution reportException(final Exception e, final AbstractStage stage) {
		if (logExceptions) {
			exceptionsList.add(e);
		}
		return onStageException(e, stage);
	}

}
