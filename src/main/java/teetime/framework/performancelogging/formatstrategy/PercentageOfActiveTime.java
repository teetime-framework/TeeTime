package teetime.framework.performancelogging.formatstrategy;

import java.util.Collection;

import teetime.framework.performancelogging.ActivationStateLogger.IFormatingStrategy;
import teetime.framework.performancelogging.StateChange;
import teetime.framework.performancelogging.StateChange.ExecutionState;
import teetime.framework.performancelogging.StateLoggable;

/**
 * Formating strategy to apply for percentage of active time.
 *
 * @author Adrian
 *
 */
public class PercentageOfActiveTime implements IFormatingStrategy {

	private final Collection<StateLoggable> stages;

	public PercentageOfActiveTime(final Collection<StateLoggable> stages) {
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
		String result = "\n	Formating of the data to get percentage of active time:\n\n";

		result += "name;% active time\n";

		for (StateLoggable stage : stages) {
			result += formateName(stage);

			boolean lastActive = false;
			long lastActiveTimestamp = Long.MAX_VALUE;
			long cumulativeActiveTime = 0;
			long firstTimestamp = Long.MAX_VALUE;
			long lastTimestamp = Long.MIN_VALUE;

			for (StateChange state : stage.getStates()) {
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
