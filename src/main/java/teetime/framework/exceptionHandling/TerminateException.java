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
package teetime.framework.exceptionHandling;

import teetime.util.StacklessException;

/**
 * Represents an Exception, which is thrown by stages in case of they import teetime.framework.Stage;
 * original exception, which was thrown, call {@link #getCause()}. {@link #getThrowingStage()} returns the stage, which has thrown the original exception.
 *
 * @since 1.1
 */
public class TerminateException extends StacklessException {

	public static final TerminateException INSTANCE = new TerminateException("Framework Exception");

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6724637605943897808L;

	private TerminateException(final String string) {
		super(string);
	};

}
