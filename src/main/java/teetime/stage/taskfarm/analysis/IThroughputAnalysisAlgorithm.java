package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.history.ThroughputHistory;

public interface IThroughputAnalysisAlgorithm {
	public double doAnalysis(ThroughputHistory history);
}
