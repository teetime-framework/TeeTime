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
package teetime.framework.performancelogging;

class CumulativeActivePassivTime implements ActivationStateLogger.IFormatingStrategy {

	/**
	 *
	 */
	private final ActivationStateLogger stateLogger;

	/**
	 * @param stateLogger
	 */
	CumulativeActivePassivTime(final ActivationStateLogger stateLogger) {
		this.stateLogger = stateLogger;
	}

	@Override
	public String formatData() {
		String result = "\n Formating to analyse differences in runtime:\n\n";

		result += "name;cumulative active time;cumulative active waiting time;cumulative blocked time; total time\n";

		// go through all the stages
		for (StateLoggable stage : this.stateLogger.getStages()) {
			// first add a formated version of their names to the line.
			result += this.stateLogger.formateName(stage);

			long earliestTimeStamp = stage.getStates().get(0).getTimeStamp();
			long latestTimeStamp = earliestTimeStamp;
			long lastTimeStamp = earliestTimeStamp;
			long cumulativeActiveTime = 0;
			long cumulativeActiveWaitingTime = 0;
			long cumulativeBlockedTime = 0;

			// go through all states of this stage and sum up the active times while counting the number of active times
			for (StateChange state : stage.getStates()) {
				long actualTimeStamp = state.getTimeStamp();

				if (actualTimeStamp < earliestTimeStamp) {
					earliestTimeStamp = actualTimeStamp;
					System.err.println("Some later State has a lower timestamp value!");
				} else if (actualTimeStamp > latestTimeStamp) {
					latestTimeStamp = actualTimeStamp;
				}

				long elapsedTime = actualTimeStamp - lastTimeStamp;

				switch (state.getExecutionState()) {
				case ACTIVE:
					cumulativeActiveTime += elapsedTime;
					break;
				case ACTIVE_WAITING:
					cumulativeActiveWaitingTime += elapsedTime;
					break;
				case BLOCKED:
					cumulativeBlockedTime += elapsedTime;
					break;
				default:
					break;
				}

				lastTimeStamp = actualTimeStamp;
			}

			result += cumulativeActiveTime + ";" + cumulativeActiveWaitingTime + ";" + cumulativeBlockedTime + ";" + (latestTimeStamp - earliestTimeStamp);

			result += "\n";
		}
		return result.replace("\n", String.format("%n"));
	}

}
