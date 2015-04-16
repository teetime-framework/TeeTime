package teetime.framework;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.InitialElementProducer;
import teetime.util.StopWatch;

public class AnalysisTest {

	private static final long DELAY_IN_MS = 500;
	private static final long ABSOLUTE_MAX_ERROR_IN_MS = 2;

	private Analysis<TestConfig> analysis;

	@Before
	public void before() {
		TestConfig configuration = new TestConfig();
		analysis = new Analysis<TestConfig>(configuration);
	}

	@Test
	public void testExecuteNonBlocking() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();
		analysis.executeNonBlocking();
		watch.end();

		assertThat(watch.getDurationInMs(), is(lessThan(DELAY_IN_MS)));
		assertFalse(analysis.getConfiguration().delay.finished);

		analysis.waitForTermination();
		assertTrue(analysis.getConfiguration().delay.finished);
	}

	@Test
	public void testExecuteBlocking() {
		StopWatch watch = new StopWatch();
		watch.start();
		analysis.executeBlocking();
		watch.end();

		assertThat(watch.getDurationInMs() + ABSOLUTE_MAX_ERROR_IN_MS, is(greaterThanOrEqualTo(DELAY_IN_MS)));
	}

	private static class TestConfig extends AnalysisConfiguration {
		final IPipeFactory intraFact = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		public final DelayAndTerminate delay;

		public TestConfig() {
			final InitialElementProducer<String> init = new InitialElementProducer<String>("Hello");
			delay = new DelayAndTerminate(DELAY_IN_MS);
			intraFact.create(init.getOutputPort(), delay.getInputPort());
			addThreadableStage(init);
		}
	}

	private static class DelayAndTerminate extends AbstractConsumerStage<String> {

		private final long delayInMs;

		public boolean finished;

		public DelayAndTerminate(final long delayInMs) {
			super();
			this.delayInMs = delayInMs;
		}

		@Override
		protected void execute(final String element) {
			try {
				Thread.sleep(delayInMs);
			} catch (InterruptedException e) {
			}
			finished = true;
		}

	}

}
