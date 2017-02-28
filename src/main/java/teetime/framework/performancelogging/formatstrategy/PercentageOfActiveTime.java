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
package teetime.framework.performancelogging.formatstrategy;

import java.util.Collection;

import teetime.framework.AbstractStage;
import teetime.framework.StateStatistics;
import teetime.framework.performancelogging.ActivationStateLogger.IFormatingStrategy;
import teetime.framework.performancelogging.StateChange;
import teetime.framework.performancelogging.StateChange.ExecutionState;

/**
 * Formating strategy to apply for percentage of active time.
 *
 * @author Adrian
 *
 */
public class PercentageOfActiveTime implements IFormatingStrategy {

	private final Collection<AbstractStage> stages;

	public PercentageOfActiveTime(final Collection<AbstractStage> stages) {
		this.stages = stages;
	}

	/**
	 * Will return the simple name of the given stage and added enough spaces to match the longest name.
	 *
	 * @param stage
	 *            Stage which name should be formated.
	 * @return Simple name of the given stage plus spaces to match the longest name.
	 */
	String formateName(final AbstractStage stage) {
		return stage.getClass().getSimpleName() + ";";
	}

	@Override
	public String formatData() {
		String result = "\n	Formating of the data to get percentage of active time:\n\n";

		result += "name;% active time\n";

		for (AbstractStage stage : stages) {
			result += formateName(stage);

			boolean lastActive = false;
			long lastActiveTimestamp = Long.MAX_VALUE;
			long cumulativeActiveTime = 0;
			long firstTimestamp = Long.MAX_VALUE;
			long lastTimestamp = Long.MIN_VALUE;

			for (StateChange state : StateStatistics.getStates(stage)) {
				if (state.getTimeStamp() < firstTimestamp) {
					firstTimestamp = state.getTimeStamp();
				}
				if (state.getTimeStamp() > lastTimestamp) {
					lastTimestamp = state.getTimeStamp();
				}
				if (!lastActive && state.getExecutionState() == ExecutionState.ACTIVE) {
					lastActive = true;
					lastActiveTimestamp = state.getTimeStamp();
				}
				if (lastActive && state.getExecutionState() != ExecutionState.ACTIVE && lastActiveTimestamp != Long.MAX_VALUE) {
					lastActive = false;
					cumulativeActiveTime += (state.getTimeStamp() - lastActiveTimestamp);
				}
			}

			result += ((double) cumulativeActiveTime / (double) (lastTimestamp - firstTimestamp)) * 100 + "\n";
		}
		return result.replace("\n", String.format("%n"));
	}
}
