package teetime.stage.taskfarm.analysis;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import teetime.stage.taskfarm.monitoring.ThroughputHistory;

public class RegressionAlgorithm extends ThroughputAnalysisAlgorithm {

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		SimpleRegression regression = new SimpleRegression();

		for (int i = 1; i <= WINDOW; i++) {
			double x = history.getEntries().get(i).getTimestamp();
			double y = history.getEntries().get(i).getThroughput();

			regression.addData(x, y);
		}

		double currentTime = history.getEntries().get(0).getTimestamp();
		double prediction = regression.predict(currentTime);

		if (prediction == Double.NaN
				|| prediction < 0
				|| prediction == Double.NEGATIVE_INFINITY
				|| prediction == Double.POSITIVE_INFINITY) {
			return 0;
		} else {
			return prediction;
		}
	}

}
