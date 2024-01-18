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

import static org.hamcrest.Matchers.*;  // NOPMD relevant for tests
import static org.junit.Assert.*;  // NOPMD relevant for tests

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.TeeTimeScheduler;

//@Ignore("test run infinitely on 20.10.17")
public class ParallelCounterConfigIT {

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
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(numThreads, config, numExecutions);
		Execution<ParallelCounterConfig> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}

}
