package teetime.framework.scheduling.globaltaskpool.experimental;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.TeeTimeService;
import teetime.framework.scheduling.globaltaskpool.GlobalTaskPoolScheduling;

@Ignore("assertions fail sporadically") // failed 20.10.17
public class ProducerConsumerGlobalTaskPoolConfigTest {

	@Test
	public void shouldExecuteProducerConsumerWithOneThread() {
		shouldExecuteProducerConsumer(1, 1);
	}

	@Test
	// @Ignore("assertion failed with 9999/10000 elements") // failed 18.08.17
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

		ProducerConsumerGlobalTaskPoolConfig config = new ProducerConsumerGlobalTaskPoolConfig(numElements, processedElements);
		TeeTimeService scheduling = new GlobalTaskPoolScheduling(numThreads, config, numExecutions);
		Execution<ProducerConsumerGlobalTaskPoolConfig> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
