package teetime.stage.taskfarm.analysis;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.analysis.WeightedAlgorithm.WeightMethod;
import teetime.stage.taskfarm.monitoring.ThroughputHistory;

/**
 * @author Christian Claus Wiechmann
 */
public class WeightedAlgorithmTest {

	private ThroughputHistory history;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final TaskFarmConfiguration configuration = new TaskFarmConfiguration(null);

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
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.EXPONENTIAL, this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.52)));
	}

	@Test
	public void linearTest() {
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LINEAR, this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.59)));
	}

	@Test
	public void logarithmicTest() {
		final AbstractThroughputAnalysisAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LOGARITHMIC, this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.63)));
	}

}
