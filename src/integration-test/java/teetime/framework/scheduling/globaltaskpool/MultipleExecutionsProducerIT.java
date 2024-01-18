/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.scheduling.globaltaskpool;

import static org.hamcrest.MatcherAssert.*; // NOPMD relevant for tests
import static org.hamcrest.Matchers.*; // NOPMD relevant for tests

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.ResponsiveProducer;

public class MultipleExecutionsProducerIT {

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
					.from(new ResponsiveProducer<String>(expectedElements))
					.end(new CollectorSink<String>(actualElements));

			GlobalTaskPoolScheduling scheduler = new GlobalTaskPoolScheduling(numThreads, configuration, numOfExecutions);
			Execution<Configuration> execution = new Execution<>(configuration, true, scheduler);
			execution.executeBlocking();

			assertThat("failed with numOfExecutions=" + numOfExecutions, actualElements, is(equalTo(expectedElements)));
		}
	}
}
