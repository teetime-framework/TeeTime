package teetime.framework.performancelogging.formatstrategy;

import java.util.Collection;

import teetime.framework.performancelogging.ActivationStateLogger.IFormatingStrategy;
import teetime.framework.performancelogging.StateChange;
import teetime.framework.performancelogging.StateChange.ExecutionState;
import teetime.framework.performancelogging.StateLoggable;

public class CumulativeActivePassivTime implements IFormatingStrategy {

	private final Collection<StateLoggable> stages;

	public CumulativeActivePassivTime(final Collection<StateLoggable> stages) {
		this.stages = stages;
	}

	/**
	 * Will return the simple name of the given stage and added enough spaces to match the longest name.
	 *
	 * @param stage
	 *            Stage which name should be formated.
	 * @return Simple name of the given stage plus spaces to match the longest name.
	 */
	String formateName(final StateLoggable stage) {
		return stage.getClass().getSimpleName() + ";";
	}

	@Override
	public String formatData() {
		String result = "\n Formating to analyse differences in runtime:\n\n";

		result += "name;total time;cumulative blocked time;cumulative active waiting time time;active time\n";

		// go through all the stages
		for (StateLoggable stage : stages) {
			// first add a formated version of their names to the line.
			result += formateName(stage);

			long earliestTimeStamp = Long.MAX_VALUE;
			long latestTimeStamp = Long.MIN_VALUE;
			long lastTimeStamp = 0;
			ExecutionState lastState = ExecutionState.INITIALIZED;
			long cumulativeActiveTime = 0;
			long cumulativeActiveWaitingTime = stage.getActiveWaitingTime();
			long cumulativeBlockedTime = 0;

			// go through all states of this stage and sum up the active times while counting the number of active timestamp
			for (StateChange state : stage.getStates()) {
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
