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

@Ignore("test run infinitely on 20.10.17")
public class ParallelCounterConfigTest {

	@Test
	public void testExecutionWithOneThread() {
		testParallelExecution(10_000, 1, 1);
	}

	@Test
	public void testExecutionWithTwoThreads() {
		testParallelExecution(10_000, 2, 1);
	}

	@Test
	public void testExecutionWithFourThreads() {
		testParallelExecution(10_000, 4, 1);
	}

	@Test
	public void testExecutionWithOneThreadWithManyExecutions() {
		testParallelExecution(10_000, 1, 256);
	}

	@Test
	public void testExecutionWithTwoThreadsWithManyExecutions() {
		testParallelExecution(10_000, 2, 256);
	}

	@Test
	public void testExecutionWithFourThreadsWithManyExecutions() {
		testParallelExecution(10_000, 4, 256);
	}

	private void testParallelExecution(final int numElements, final int numThreads, final int numExecutions) {
		List<Integer> processedElements = new ArrayList<>();

		ParallelCounterConfig config = new ParallelCounterConfig(numElements, numThreads, processedElements);
		TeeTimeService scheduling = new GlobalTaskPoolScheduling(numThreads, config, numExecutions);
		Execution<ParallelCounterConfig> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}

}
