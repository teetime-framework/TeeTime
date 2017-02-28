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
 * Formating Strategy to apply the data to the Bottleneck Detection Approach of Roser, Nakano and Tanaka.
 *
 * @author Adrian
 *
 */
public class RNTFormating implements IFormatingStrategy {

	private final Collection<AbstractStage> stages;

	public RNTFormating(final Collection<AbstractStage> stages) {
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
		String result = "\n	Formating of the data to fit Approach of Roser, Nakano and Tanak:\n\n";

		result += "name;Average ActiveTime (ns)\n";

		// go through all the stages
		for (AbstractStage stage : stages) {
			// first add a formated version of their names to the line.
			result += formateName(stage);

			// will count the number of activeTimes
			double counter = 0;
			// stores the sum of active time
			long cummulativeActiveTime = 0;
			// stores the last relevant active time stamp
			long lastActiveTimeStamp = 0;
			// boolean to remember last state that was processed
			boolean lastActive = false;

			// go through all states of this stage and sum up the active times while counting the number of active times
			for (StateChange state : StateStatistics.getStates(stage)) {
				if (state.getExecutionState() == ExecutionState.ACTIVE && !lastActive) {
					lastActiveTimeStamp = state.getTimeStamp();
					lastActive = true;
				} else {
					if (lastActive && lastActiveTimeStamp != 0) {
						cummulativeActiveTime += (state.getTimeStamp() - lastActiveTimeStamp);
						counter++;
					}
					lastActive = false;
				}
			}

			// Add formated data to the line
			result +=
					// I differentiate between stages that were active the whole time and the ones that were interrupted in between.
					// this will help to keep track of the necessary information.
					((counter != 0) ? ((long) ((cummulativeActiveTime) / counter)) : cummulativeActiveTime);

			result += "\n";
		}
		return result.replace("\n", String.format("%n"));
	}

};
