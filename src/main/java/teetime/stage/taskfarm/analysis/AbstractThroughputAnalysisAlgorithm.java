package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.monitoring.ThroughputHistory;

/**
 * This abstract class calculates the Throughput analysis score
 * by means of the prediction value of the implemented Throughput
 * Algorithm.
 *
 * @author Christian Claus Wiechmann
 *
 */
public abstract class AbstractThroughputAnalysisAlgorithm {

	/**
	 * Number of recently measured throughput values that we will analyze.
	 */
	protected final int window;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public AbstractThroughputAnalysisAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		this.window = configuration.getAnalysisWindow();
	}

	/**
	 * Calculates a prediction for the most recent ThroughputEntry by means
	 * of the last "window" entries.
	 *
	 * @param history
	 *            measured throughput values + timestamps
	 * @return throughput prediction for the most recent value
	 */
	protected abstract double doAnalysis(ThroughputHistory history);

	/**
	 * Calculates the throughput score for a given history.
	 *
	 * @param history
	 *            history of measured values
	 * @return throughput score
	 */
	public double getTroughputAnalysis(final ThroughputHistory history) {
		if (!this.isHistoryLargeEnough(history)) {
			return 0;
		}

		final double predicted = this.doAnalysis(history);
		final double lastValue = history.getThroughputOfEntry(0);

		final double difference = predicted - lastValue;
		final double sum = predicted + lastValue;

		if (sum == 0) {
			return 0;
		} else {
			return Math.abs(difference / sum);
		}
	}

	private boolean isHistoryLargeEnough(final ThroughputHistory history) {
		return history.getEntries().size() >= this.window + 1;
	}
}
