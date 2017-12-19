package teetime.framework.scheduling.globaltaskpool;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.TeeTimeService;
import teetime.framework.scheduling.globaltaskpool.GlobalTaskPoolScheduling;
import teetime.stage.CollectorSink;
import teetime.stage.StreamProducer;

public class ProducerConsumerGlobalTaskPoolIT {

	@Test
	public void shouldExecuteProducerConsumerWithOneThread() {
		shouldExecuteProducerConsumer(1, 1);
	}

	@Test
	public void shouldExecuteProducerConsumerWithTwoThreads() {
		shouldExecuteProducerConsumer(2, 1);
	}

	@Test
	public void shouldExecuteProducerConsumerWithFourThreads() {
		shouldExecuteProducerConsumer(4, 1);
	}

	@Test
	public void shouldExecuteProducerConsumerWithOneThreadMultipleExecutions() {
		shouldExecuteProducerConsumer(1, 256);
	}

	@Test
	public void shouldExecuteProducerConsumerWithTwoThreadsMultipleExecutions() {
		shouldExecuteProducerConsumer(2, 256);
	}

	@Test
	public void shouldExecuteProducerConsumerWithFourThreadsMultipleExecutions() {
		shouldExecuteProducerConsumer(4, 256);
	}

	private void shouldExecuteProducerConsumer(final int numThreads, final int numExecutions) {
		int numElements = 10_000;
		List<Integer> processedElements = new ArrayList<>();

		IntStream inputElements = IntStream.iterate(0, i -> i + 1).limit(numElements);
		Configuration config = new Configuration()
				.from(new StreamProducer<>(inputElements))
				.end(new CollectorSink<>(processedElements));

		TeeTimeService scheduling = new GlobalTaskPoolScheduling(numThreads, config, numExecutions);
		Execution<Configuration> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
