package teetime.stage.taskfarm.analysis;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.analysis.WeightedAlgorithm.WeightMethod;
import teetime.stage.taskfarm.monitoring.ThroughputHistory;

public class WeightedAlgorithmTest {

	ThroughputHistory history;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final TaskFarmConfiguration configuration = new TaskFarmConfiguration(null);

	@Before
	public void prepareHistory() {
		history = new ThroughputHistory();

		history.add(1);
		history.add(10);
		history.add(1);
		history.add(1);
	}

	@Test
	public void exponentialTest() {
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.EXPONENTIAL, configuration);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(greaterThan(0.52)));
	}

	@Test
	public void linearTest() {
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LINEAR, configuration);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(greaterThan(0.59)));
	}

	@Test
	public void logarithmicTest() {
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LOGARITHMIC, configuration);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(greaterThan(0.63)));
	}

}
