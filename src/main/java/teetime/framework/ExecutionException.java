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

import java.util.Collection;

import teetime.util.ThreadThrowableContainer;

/**
 * Represents a exception, which is thrown by an analysis, if any problems occured within its execution.
 * A collection of thrown exceptions within the analysis can be retrieved with {@link #getThrownExceptions()}.
 *
 * @since 1.1
 */
public class ExecutionException extends RuntimeException {

	private static final long serialVersionUID = 7486086437171884298L;

	private final Collection<ThreadThrowableContainer> exceptions;

	public ExecutionException(final Collection<ThreadThrowableContainer> exceptions) {
		super("Error(s) while execution. Check thrown exception(s).");
		this.exceptions = exceptions;
	}

	/**
	 * Returns all exceptions thrown within the analysis.
	 * These are passed on as pairs of threads and throwables, to indicate a exception's context.
	 *
	 * @return a collection of pairs
	 */
	public Collection<ThreadThrowableContainer> getThrownExceptions() {
		return exceptions;
	}

}
