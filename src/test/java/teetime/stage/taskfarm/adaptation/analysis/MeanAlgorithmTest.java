package teetime.stage.taskfarm.adaptation.analysis;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.taskfarm.adaptation.monitoring.ThroughputHistory;

public class MeanAlgorithmTest {

	private static final double EPSILON = 0.00001;

	@Test
	public void constantThoughputTestAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(40.5);
		history.add(40.8);
		history.add(40.8);
		history.add(60);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(greaterThan(0.1)));
	}

	@Test
	public void constantThoughputTestNoAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4.5);
		history.add(4.8);
		history.add(4.8);
		history.add(4.7);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void risingThoughputTestNoAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(7);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(greaterThan(0.1)));
	}

	@Test
	public void risingThoughputTestAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(3);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(closeTo(0.25d, EPSILON)));
	}

	@Test
	public void irregularThoughputTest() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(10);
		history.add(8);
		history.add(1);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(greaterThan(0.7)));
	}
}
