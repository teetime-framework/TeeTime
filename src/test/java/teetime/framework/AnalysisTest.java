package teetime.framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.InitialElementProducer;
import teetime.util.StopWatch;

public class AnalysisTest {

	private Analysis analysis;
	private TestConfig configuration;

	@Test
	public void testExecuteNonBlocking() throws Exception {
		newInstances();
		analysis.executeNonBlocking();
		assertFalse(configuration.delay.finished);
		analysis.waitForTermination();
		assertTrue(configuration.delay.finished);
	}

	@Test
	public void testExecuteBlocking() {
		StopWatch watch = new StopWatch();
		newInstances();
		watch.start();
		analysis.executeBlocking();
		watch.end();
		assertTrue(watch.getDurationInNs() >= 500000000);
	}

	private void newInstances() {
		configuration = new TestConfig();
		analysis = new Analysis(configuration);
	}

	private class TestConfig extends AnalysisConfiguration {
		final IPipeFactory intraFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		public final DelayAndTerminate delay;

		public TestConfig() {
			final InitialElementProducer<String> init = new InitialElementProducer<String>("Hello");
			delay = new DelayAndTerminate();
			intraFact.create(init.getOutputPort(), delay.getInputPort());
			addThreadableStage(init);
		}
	}

	private class DelayAndTerminate extends AbstractConsumerStage<String> {

		public boolean finished;

		@Override
		protected void execute(final String element) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			finished = true;
		}

	}

}
