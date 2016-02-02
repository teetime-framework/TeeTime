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
package teetime.stage.taskfarm.exception;

/**
 * Represents an exception thrown by the task farm. It
 * gets thrown if the user tries to monitor a pipe which
 * does not implement {@link teetime.framework.pipe.IMonitorablePipe IMonitorablePipe}.
 *
 * @author Christian Claus Wiechmann
 */
public class TaskFarmInvalidPipeException extends RuntimeException {

	private static final long serialVersionUID = 219434999064433531L;

	/**
	 * Represents an exception thrown by the task farm. It
	 * gets thrown if the user tries to monitor a pipe which
	 * does not implement {@link teetime.framework.pipe.IMonitorablePipe IMonitorablePipe}.
	 *
	 * @param s
	 *            error message
	 */
	public TaskFarmInvalidPipeException(final String s, final Throwable cause) {
		super(s, cause);
	}

}
