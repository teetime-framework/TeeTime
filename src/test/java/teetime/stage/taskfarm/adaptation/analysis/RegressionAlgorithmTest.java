package teetime.stage.taskfarm.adaptation.analysis;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.adaptation.monitoring.ThroughputHistory;

public class RegressionAlgorithmTest {

	private static final double EPSILON = 0.1d;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final TaskFarmConfiguration configuration = new TaskFarmConfiguration(null);

	@Test
	public void positiveRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(1);
		Thread.sleep(100);
		history.add(2);
		Thread.sleep(100);
		history.add(3);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(5);

		final AbstractThroughputAnalysisAlgorithm algorithm = new RegressionAlgorithm(this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void negativeRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(8);
		Thread.sleep(100);
		history.add(6);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(2);

		final AbstractThroughputAnalysisAlgorithm algorithm = new RegressionAlgorithm(this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void boundedRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(7);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(1);
		Thread.sleep(100);
		history.add(0);

		final AbstractThroughputAnalysisAlgorithm algorithm = new RegressionAlgorithm(this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void falseRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(9);
		Thread.sleep(100);
		history.add(8);
		Thread.sleep(100);
		history.add(7);
		Thread.sleep(100);
		history.add(13);

		final AbstractThroughputAnalysisAlgorithm algorithm = new RegressionAlgorithm(this.configuration);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(greaterThan(0.3d)));
	}

}
