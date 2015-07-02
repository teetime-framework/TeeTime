package teetime.stage.taskfarm.analysis;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.ThroughputHistory;

public class MeanAlgorithmTest {

	private static final double DELTA = 0.00001;

	@Test
	public void constantThoughputTestAnomaly() {
		ThroughputHistory history = new ThroughputHistory();

		history.add(40.5);
		history.add(40.8);
		history.add(40.8);
		history.add(60);

		ThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm();
		double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertTrue(thoughputScore > 0.1);
	}

	@Test
	public void constantThoughputTestNoAnomaly() {
		ThroughputHistory history = new ThroughputHistory();

		history.add(4.5);
		history.add(4.8);
		history.add(4.8);
		history.add(4.7);

		ThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm();
		double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertTrue(isAbout(thoughputScore, 0));
	}

	@Test
	public void risingThoughputTestNoAnomaly() {
		ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(7);

		ThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm();
		double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertTrue(thoughputScore > 0.1);
	}

	@Test
	public void risingThoughputTestAnomaly() {
		ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(3);

		ThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm();
		double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertTrue(isAbout(thoughputScore, 0.25));
	}

	@Test
	public void irregularThoughputTest() {
		ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(10);
		history.add(8);
		history.add(1);

		ThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm();
		double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertTrue(thoughputScore > 0.7);
	}

	private boolean isAbout(final double is, final double should) {
		double difference = Math.abs(is - should);
		return difference < DELTA;
	}
}
