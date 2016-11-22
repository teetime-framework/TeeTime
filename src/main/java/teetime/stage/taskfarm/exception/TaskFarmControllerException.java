/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.exception;

/**
 * Represents an exception thrown by the task farm reconfiguration component. It
 * gets thrown if the reconfiguration component is not able to add or remove a
 * worker stage.
 *
 * @author Christian Claus Wiechmann
 */
public class TaskFarmControllerException extends RuntimeException {

	private static final long serialVersionUID = 7394932513863758925L;

	/**
	 * Represents an exception thrown by the task farm reconfiguration component. It
	 * gets thrown if the reconfiguration component is not able to add or remove a
	 * worker stage.
	 *
	 * @param message
	 *            error message
	 */
	public TaskFarmControllerException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
