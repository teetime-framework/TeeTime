package teetime.framework.performancelogging;

import teetime.framework.performancelogging.ActivationStateLogger.IFormatingStrategy;

/**
 * Formating Strategy to apply the data to the Bottleneck Detection Approach of Roser, Nakano and Tanaka.
 */
class RNTFormating implements IFormatingStrategy {
	/**
	 *
	 */
	private final ActivationStateLogger stateLogger;

	/**
	 * @param stateLogger
	 */
	RNTFormating(final ActivationStateLogger stateLogger) {
		this.stateLogger = stateLogger;
	}

	@Override
	public String formatData() {
		String result = "\n	Formating of the data to fit Approach of Roser, Nakano and Tanak:\n\n";

		// go through all the stages
		for (StateLoggable stage : this.stateLogger.getStages()) {
			// first add a formated version of their names to the line.
			result += this.stateLogger.formateName(stage);

			// will count the number of activeTimes
			double counter = 0;
			// stores the sum of active time
			long commulativeActiveTime = 0;
			// stores the last relevant active time stamp
			long lastActiveTimeStamp = 0;
			// boolean to remember last state that was processed
			boolean lastActive = false;

			// go through all states of this stage and sum up the active times while counting the number of active times
			for (ActivationState state : stage.getStates()) {
				if (state.getCause() == ActivationState.NOTHING_FAILED) {
					if (!lastActive) {
						lastActiveTimeStamp = state.getTimeStamp();
					}
					lastActive = true;
				} else {
					if (lastActive && lastActiveTimeStamp != 0) {
						commulativeActiveTime += (state.getTimeStamp() - lastActiveTimeStamp);
						counter++;
					}
					lastActive = false;
				}
			}

			// Add formated data to the line
			result += " Average ActiveTime in seconds: "
					// I differentiate between stages that were active the whole time and the ones that were interrupted in between.
					// this will help to keep track of the necessary information.
					+ ((counter != 0) ? (((commulativeActiveTime) / counter) / 1000000) : "whole time");

			result += "\n";
		}
		return result.replace("\n", String.format("%n"));
	}

}
