package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.monitoring.ThroughputHistory;

public abstract class ThroughputAnalysisAlgorithm {

	protected final static int WINDOW = 3;

	protected abstract double doAnalysis(ThroughputHistory history);

	public double getTroughputAnalysis(final ThroughputHistory history) {
		if (!isHistoryLargeEnough(history)) {
			return 0;
		}

		double predicted = doAnalysis(history);
		double lastValue = history.getEntries().get(0).getThroughput();

		double difference = predicted - lastValue;
		double sum = predicted + lastValue;

		if (sum == 0) {
			return 0;
		} else {
			return Math.abs(difference / sum);
		}
	}

	protected boolean isHistoryLargeEnough(final ThroughputHistory history) {
		return history.getEntries().size() >= WINDOW + 1;
	}
}
