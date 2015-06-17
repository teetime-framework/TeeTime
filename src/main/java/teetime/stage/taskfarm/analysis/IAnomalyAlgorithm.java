package teetime.stage.taskfarm.analysis;

import teetime.stage.taskfarm.history.ThroughputHistory;

public interface IAnomalyAlgorithm {
	public double doAnomalyScore(ThroughputHistory history);
}
