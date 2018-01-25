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
package teetime.framework.scheduling.globaltaskpool;

import java.util.Collection;

import teetime.framework.AbstractStage;

/**
 * Represents a queue which can be used by the scheduler.
 * By definition, this data structure is ordered and potentially prioritized.
 *
 * @author Christian Wulf (chw)
 *
 * @since 3.0
 */
public interface ScheduleQueue {

	/**
	 * @return and removes the next stage from this queue, or <code>null</code> otherwise.
	 */
	AbstractStage removeNextStage();

	/**
	 * @param stage
	 *            to be scheduled
	 * @return <code>true</code> iff the given stage could be scheduled, otherwise <code>false</code>.
	 */
	boolean scheduleStage(final AbstractStage stage);

	/**
	 * @param stages
	 *            to be scheduled
	 * @return <code>true</code> iff all of the given stages could be scheduled, otherwise <code>false</code>.
	 */
	default boolean scheduleStages(final Collection<? extends AbstractStage> stages) {
		boolean scheduledAllStages = true;
		for (AbstractStage stage : stages) {
			boolean scheduledStage = scheduleStage(stage);
			scheduledAllStages = scheduledAllStages && scheduledStage;
		}
		return scheduledAllStages;
	}

}
