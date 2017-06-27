package teetime.framework.scheduling.globaltaskqueue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.ConfigurationContext;
import teetime.framework.Execution;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.InitialElementProducer;

public class PipelineTest {

	private static class GlobalTaskQueueConfig<T> extends Configuration {

		private static final int NUM_THREADS = 4;
		private static final GlobalTaskQueueScheduling SCHEDULER = new GlobalTaskQueueScheduling(NUM_THREADS);

		public GlobalTaskQueueConfig(final T... elements) {
			super(new TerminatingExceptionListenerFactory(), new ConfigurationContext(SCHEDULER));
			SCHEDULER.setConfiguration(this);
			build(elements);
		}

		private void build(final T... elements) {
			InitialElementProducer<T> producer = new InitialElementProducer<>(elements);
			Counter<T> counter = new Counter<>();
			CollectorSink<T> sink = new CollectorSink<>();

			from(producer).to(counter).end(sink);
		}
	}

	@Test
	@Ignore
	public void shouldExecutePipelineCorrectly() throws Exception {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskQueueConfig<String> config = new GlobalTaskQueueConfig<>(inputElements);
		Execution<GlobalTaskQueueConfig<String>> execution = new Execution<>(config);
		execution.executeBlocking();

		String[] expectedElements = { "a", "b", "c" };
		assertThat(inputElements, is(equalTo(expectedElements)));
	}
}
