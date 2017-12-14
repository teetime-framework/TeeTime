package teetime.framework.scheduling.globaltaskpool;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class OneExecutionProducerIT {

	@Test
	public void testRegularExecutionWithOneThread() throws Exception {
		List<String> expectedElements = Arrays.asList("a", "b", "c");

		int numThreads = 1;
		testRegularExecution(expectedElements, numThreads);
	}

	@Test
	public void testRegularExecutionWithMultipleThreads() throws Exception {
		List<String> expectedElements = Arrays.asList("a", "b", "c");

		int numThreads = 2;
		testRegularExecution(expectedElements, numThreads);
	}

	void testRegularExecution(final List<String> expectedElements, final int numThreads) {
		for (int numOfExecutions = 1; numOfExecutions < expectedElements.size() + 1; numOfExecutions++) {
			List<String> actualElements = new ArrayList<>();

			Configuration configuration = new Configuration()
					.from(new InitialElementProducer<String>(expectedElements))
					.end(new CollectorSink<String>(actualElements));

			GlobalTaskPoolScheduling scheduler = new GlobalTaskPoolScheduling(numThreads, configuration, numOfExecutions);
			Execution<Configuration> execution = new Execution<>(configuration, true, scheduler);
			execution.executeBlocking();

			assertThat("failed with numOfExecutions=" + numOfExecutions, actualElements, is(equalTo(expectedElements)));
		}
	}
}
