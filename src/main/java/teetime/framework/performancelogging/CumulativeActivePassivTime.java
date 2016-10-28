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
			for (ActivationState state : stage.getStates()) {
				long actualTimeStamp = state.getTimeStamp();

				if (actualTimeStamp < earliestTimeStamp) {
					earliestTimeStamp = actualTimeStamp;
					System.err.println("Some later State has a lower timestamp value!");
				} else if (actualTimeStamp > latestTimeStamp) {
					latestTimeStamp = actualTimeStamp;
				}

				long elapsedTime = actualTimeStamp - lastTimeStamp;

				switch (state.getState()) {
				case ActivationState.ACTIV:
					cumulativeActiveTime += elapsedTime;
					break;
				case ActivationState.ACTIV_WAITING:
					cumulativeActiveWaitingTime += elapsedTime;
					break;
				case ActivationState.BLOCKED:
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
