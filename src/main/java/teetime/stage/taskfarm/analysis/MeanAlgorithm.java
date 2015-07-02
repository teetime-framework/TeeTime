package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.history.ThroughputHistory;

public class MeanAlgorithm extends ThroughputAnalysisAlgorithm {

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double sumOfHistoryValues = 0;

		for (int i = 1; i <= WINDOW; i++)
		{
			double currentHistoryValue = history.getEntries().get(i).getThroughput();
			sumOfHistoryValues += currentHistoryValue;
		}

		double prediction = sumOfHistoryValues / WINDOW;
		return prediction;
	}
}
