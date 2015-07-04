package teetime.stage.taskfarm.analysis;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.monitoring.ThroughputHistory;

/**
 * RegressionAlgorithm analyzes the throughput of a certain amount of
 * items and uses a linear regression analysis to predict the next value.
 * This algorithm is more exact than MeanAlgorithm and WeightedAlgorithm,
 * because it uses the timestamp instead of the relative positioning
 * of ThroughputHistory items for its calculations.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class RegressionAlgorithm extends AbstractThroughputAnalysisAlgorithm {

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public RegressionAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		final SimpleRegression regression = new SimpleRegression();

		for (int i = 1; i <= window; i++) {
			final double xaxis = history.getTimestampOfEntry(i);
			final double yaxis = history.getThroughputOfEntry(i);

			regression.addData(xaxis, yaxis);
		}

		final double currentTime = history.getTimestampOfEntry(0);
		double prediction = regression.predict(currentTime);

		if (Double.isNaN(prediction)
				|| prediction < 0
				|| Double.isInfinite(prediction)) {
			prediction = 0;
		}

		return prediction;
	}

}
