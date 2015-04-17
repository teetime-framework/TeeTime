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

import teetime.framework.Stage;

/**
 * Represents an Exception, which is thrown by stages in case of they throw exceptions.
 * To get the original exception, which was thrown, call {@link #getCause()}. {@link #getThrowingStage()} returns the stage, which has thrown the original exception.
 *
 * @since 1.1
 */
public class StageException extends RuntimeException {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6724637605943897808L;

	private final Stage throwingStage;

	public StageException(final Exception e, final Stage throwingStage) {
		super(e);
		this.throwingStage = throwingStage;
	}

	/**
	 * Returns the stage, which failed with an uncatched exception
	 *
	 * @return stage instance, which throws the exception
	 */
	public Stage getThrowingStage() {
		return throwingStage;
	}

}
