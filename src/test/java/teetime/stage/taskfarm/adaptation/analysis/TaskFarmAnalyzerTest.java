package teetime.stage.taskfarm.adaptation.analysis;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.MeanAlgorithm;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.RegressionAlgorithm;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

public class TaskFarmAnalyzerTest {

	@SuppressWarnings("rawtypes")
	private final TaskFarmConfiguration configuration = createConfiguration();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final TaskFarmAnalyzer analyzer = new TaskFarmAnalyzer(configuration);
	private final ThroughputHistory history = new ThroughputHistory();
	private double throughputScore = 0;

	@Test
	public void analyzerTest() {
		checkAnalyzerWithAlgorithm("MeanAlgorithm", MeanAlgorithm.class);
		checkAnalyzerWithAlgorithm("WeightedAlgorithm", WeightedAlgorithm.class);
		checkAnalyzerWithAlgorithm("RegressionAlgorithm", RegressionAlgorithm.class);
	}

	@SuppressWarnings("rawtypes")
	private void checkAnalyzerWithAlgorithm(final String algorithmName, final Class algorithmClass) {
		configuration.setThroughputAlgorithm(algorithmName);
		analyzer.analyze(history);
		throughputScore = analyzer.getThroughputScore();
		assertThat(throughputScore, is(equalTo((double) AbstractThroughputAnalysisAlgorithm.INVALID_SCORE)));
		assertThat(analyzer.getLastUsedAlgorithm(), is(instanceOf(algorithmClass)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TaskFarmConfiguration createConfiguration() {
		TaskFarmStage taskFarmStage = new TaskFarmStage(new DummyDuplicableStage());
		TaskFarmConfiguration configuration = taskFarmStage.getConfiguration();
		return configuration;
	}

	@SuppressWarnings("rawtypes")
	private class DummyDuplicableStage extends AbstractFilter implements ITaskFarmDuplicable {
		@Override
		public ITaskFarmDuplicable duplicate() {
			return null;
		}

		@Override
		protected void execute(final Object element) {
			// nothing to do
		}
	}
}
