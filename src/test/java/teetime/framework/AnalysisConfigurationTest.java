package teetime.framework;

import org.junit.Assert;
import org.junit.Test;

import teetime.stage.Clock;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class AnalysisConfigurationTest {

	@Test
	public void checkIfCorrectAdded() {
		AnalysisConfiguration config = new AnalysisConfiguration();

		// Consumer -> BY_SIGNAL
		Counter<String> counter = new Counter<String>();
		config.addThreadableStage(counter);

		// Infinite producer -> BY_INTERRUPT
		Clock clock = new Clock();
		config.addThreadableStage(clock);

		// Finite Producer -> BY_SELF_DECISION
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(1, 2, 3, 4);
		config.addThreadableStage(producer);

		config.init();
		Assert.assertTrue(config.getConsumerStages().remove(counter));
		Assert.assertTrue(config.getInfiniteProducerStages().remove(clock));
		Assert.assertTrue(config.getFiniteProducerStages().remove(producer));
	}
}
