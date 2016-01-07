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
package teetime.stage.taskfarm.adaptation.reconfiguration;

/**
 * Represents the current plan of the task farm reconfiguration service.
 * Possible values are addition or removal of a stage. Furthermore, no
 * action at all can be taken.
 *
 * @author Christian Claus Wiechmann
 */
enum TaskFarmReconfigurationCommand {
	/** Represents the addition of a new stage. **/
	ADD,
	/** Represents the removal of an existing stage. **/
	REMOVE,
	/** Represents that no action at all should be taken. **/
	NONE
}
