package teetime.stage.taskfarm.analysis;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.taskfarm.analysis.WeightedAlgorithm.WeightMethod;
import teetime.stage.taskfarm.history.ThroughputHistory;

public class WeightedAlgorithmTest {

	ThroughputHistory history;

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
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.EXPONENTIAL);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertTrue(thoughputScore > 0.52);
	}

	@Test
	public void linearTest() {
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LINEAR);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertTrue(thoughputScore > 0.59);
	}

	@Test
	public void logarithmicTest() {
		ThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LOGARITHMIC);
		double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertTrue(thoughputScore > 0.63);
	}

}
