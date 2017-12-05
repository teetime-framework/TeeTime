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
package teetime.framework.scheduling;

import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.AbstractUnsynchedPipe;

public interface PipeScheduler {

	/**
	 * This event is invoked by the given <b>unsynchronized</b> pipe whenever a new element was added to it.
	 *
	 * @param pipe
	 */
	void onElementAdded(AbstractUnsynchedPipe<?> pipe);

	/**
	 * This event is invoked by the given <b>synchronized</b> pipe whenever a new element was added to it.
	 *
	 * @param pipe
	 */
	void onElementAdded(AbstractSynchedPipe<?> pipe);

	/**
	 * This event is invoked by the given <b>synchronized</b> pipe whenever a new element could not be added to it.
	 *
	 * @param pipe
	 */
	void onElementNotAdded(AbstractSynchedPipe<?> pipe);

}
