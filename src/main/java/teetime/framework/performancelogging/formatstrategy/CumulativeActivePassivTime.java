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

public class CumulativeActivePassivTime implements IFormatingStrategy {

	private final Collection<AbstractStage> stages;

	public CumulativeActivePassivTime(final Collection<AbstractStage> stages) {
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
		String result = "\n Formating to analyse differences in runtime:\n\n";

		result += "name;total time;cumulative blocked time;cumulative active waiting time time;active time\n";

		// go through all the stages
		for (AbstractStage stage : stages) {
			// first add a formated version of their names to the line.
			result += formateName(stage);

			long earliestTimeStamp = Long.MAX_VALUE;
			long latestTimeStamp = Long.MIN_VALUE;
			long lastTimeStamp = 0;
			ExecutionState lastState = ExecutionState.INITIALIZED;
			long cumulativeActiveTime = 0;
			long cumulativeActiveWaitingTime = StateStatistics.getActiveWaitingTime(stage);
			long cumulativeBlockedTime = 0;

			// go through all states of this stage and sum up the active times while counting the number of active timestamp
			for (StateChange state : StateStatistics.getStates(stage)) {
				long actualTimeStamp = state.getTimeStamp();

				// update earliest and latest timeStamp if necessary
				if (actualTimeStamp < earliestTimeStamp) {
					earliestTimeStamp = actualTimeStamp;
				} else if (actualTimeStamp > latestTimeStamp) {
					latestTimeStamp = actualTimeStamp;
				}

				// In the first loop neither lastTimeStamp nor lastState are set. So the next part wouldn't calculate correct.
				if (lastState != ExecutionState.INITIALIZED) {
					long elapsedTime = actualTimeStamp - lastTimeStamp;

					switch (lastState) {
					case ACTIVE:
						cumulativeActiveTime += elapsedTime;
						break;
					case BLOCKED:
						cumulativeBlockedTime += elapsedTime;
						break;
					case TERMINATED:
						break;
					default:
						break;
					}
				}

				lastTimeStamp = actualTimeStamp;
				lastState = state.getExecutionState();
			}

			// The ActiveWaiting time was counted into active time till now. So it it subtracted now.
			cumulativeActiveTime -= cumulativeActiveWaitingTime;

			result += (latestTimeStamp - earliestTimeStamp) + ";" + cumulativeBlockedTime + ";" + cumulativeActiveWaitingTime + ";" + cumulativeActiveTime;
			result += "\n";

		}
		return result.replace("\n", String.format("%n"));
	}

}
