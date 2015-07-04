package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.monitoring.ThroughputHistory;

/**
 * MeanAlgorithm analyzes the throughput of a certain amount of
 * items and predicts the next value based on the mean value.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class MeanAlgorithm extends AbstractThroughputAnalysisAlgorithm {

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public MeanAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double sum = 0;

		for (int i = 1; i <= window; i++) {
			final double current = history.getThroughputOfEntry(i);
			sum += current;
		}

		return sum / window;
	}
}
