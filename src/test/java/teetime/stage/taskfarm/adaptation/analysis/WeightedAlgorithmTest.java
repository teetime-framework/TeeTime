package teetime.stage.taskfarm.adaptation.analysis;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.taskfarm.adaptation.analysis.WeightedAlgorithm.WeightMethod;
import teetime.stage.taskfarm.adaptation.monitoring.ThroughputHistory;

/**
 * @author Christian Claus Wiechmann
 */
public class WeightedAlgorithmTest {

	private ThroughputHistory history;

	@Before
	public void prepareHistory() {
		this.history = new ThroughputHistory();

		this.history.add(1);
		this.history.add(10);
		this.history.add(1);
		this.history.add(1);
	}

	@Test
	public void exponentialTest() {
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.EXPONENTIAL, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.52)));
	}

	@Test
	public void linearTest() {
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LINEAR, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.59)));
	}

	@Test
	public void logarithmicTest() {
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LOGARITHMIC, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.63)));
	}

}
