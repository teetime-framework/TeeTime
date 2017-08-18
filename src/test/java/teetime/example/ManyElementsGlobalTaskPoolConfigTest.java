package teetime.example;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.TeeTimeService;
import teetime.framework.scheduling.globaltaskpool.GlobalTaskPoolScheduling;

public class ManyElementsGlobalTaskPoolConfigTest {

	@Test
	// @Ignore("assertion failed with 9999/10000 elements")
	public void shouldExecutePipelineCorrectlyManyElements() {
		int numElements = 10_000;
		List<Integer> processedElements = new ArrayList<>();
		int numThreads = 4;

		ManyElementsGlobalTaskPoolConfig config = new ManyElementsGlobalTaskPoolConfig(numElements, processedElements);
		TeeTimeService scheduling = new GlobalTaskPoolScheduling(numThreads, config, 1);
		Execution<ManyElementsGlobalTaskPoolConfig> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
