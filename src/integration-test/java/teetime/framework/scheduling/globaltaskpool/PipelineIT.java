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

import static org.hamcrest.Matchers.*; // NOPMD relevant for tests
import static org.junit.Assert.*; // NOPMD relevant for tests

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.TeeTimeScheduler;

public class PipelineIT {

	private static final String WRONG_PIPELINE_EXECUTION = "Wrong pipeline execution";
	private static final int NUM_THREADS = 4;

	private static class GlobalTaskPoolConfig<T> extends Configuration {
		private final CollectorSink<T> sink;

		@SafeVarargs
		public GlobalTaskPoolConfig(final T... elements) {
			InitialElementProducer<T> producer = new InitialElementProducer<>(elements);
			Counter<T> counter = new Counter<>();
			// StatelessCounter<T> counter = new StatelessCounter<>();
			// NoopFilter<T> noopFilter = new NoopFilter<>();
			sink = new CollectorSink<>();
			// from(producer).to(counter).to(noopFilter).end(sink);
			from(producer).to(counter).end(sink);
		}

		public CollectorSink<T> getSink() {
			return sink;
		}
	}

	private static class ManyElementsWithStatelessStageGlobalTaskPoolConfig extends Configuration {

		private final CollectorSink<Integer> sink;

		public ManyElementsWithStatelessStageGlobalTaskPoolConfig(final int numInputObjects) {
			IntStream inputElements = IntStream.iterate(0, i -> i + 1).limit(numInputObjects);
			sink = new CollectorSink<>();
			from(new StreamProducer<>(inputElements))
					.to(new StatelessCounter<>())
					.to(new NoopFilter<>())
					.end(sink);
		}

		public CollectorSink<Integer> getSink() {
			return sink;
		}
	}

	@Test
	public void shouldExecutePipelineCorrectlyThreeElements() {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskPoolConfig<String> config = new GlobalTaskPoolConfig<>(inputElements);
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(NUM_THREADS, config, 1);
		Execution<GlobalTaskPoolConfig<String>> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		List<String> processedElements = config.getSink().getElements();
		List<String> expectedElements = Arrays.asList("a", "b", "c");
		assertThat(WRONG_PIPELINE_EXECUTION, processedElements, is(equalTo(expectedElements)));
	}

	@Test
	// FIXME stopped 22.12.2017
	public void shouldExecutePipelineCorrectlyThreeElementsWithOneSingleThread() {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskPoolConfig<String> config = new GlobalTaskPoolConfig<>(inputElements);
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(1, config, 1);
		Execution<GlobalTaskPoolConfig<String>> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		List<String> processedElements = config.getSink().getElements();
		List<String> expectedElements = Arrays.asList("a", "b", "c");
		assertThat(WRONG_PIPELINE_EXECUTION, processedElements, is(equalTo(expectedElements)));
	}

	@Test
	public void shouldExecutePipelineCorrectlyThreeElementsWith2ExecutionsPerTask() {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskPoolConfig<String> config = new GlobalTaskPoolConfig<>(inputElements);
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(NUM_THREADS, config, 2);
		Execution<GlobalTaskPoolConfig<String>> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		List<String> processedElements = config.getSink().getElements();
		List<String> expectedElements = Arrays.asList("a", "b", "c");
		assertThat(WRONG_PIPELINE_EXECUTION, processedElements, is(equalTo(expectedElements)));
	}

	@Test
	public void shouldExecutePipelineCorrectlyThreeElementsWith3ExecutionsPerTask() {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskPoolConfig<String> config = new GlobalTaskPoolConfig<>(inputElements);
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(NUM_THREADS, config, 3);
		Execution<GlobalTaskPoolConfig<String>> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		List<String> processedElements = config.getSink().getElements();
		List<String> expectedElements = Arrays.asList("a", "b", "c");
		assertThat(WRONG_PIPELINE_EXECUTION, processedElements, is(equalTo(expectedElements)));
	}

	@Test
	@Ignore("The reflexive pipe in the counter is not handled correctly by the scheduling strategy so far")
	public void shouldExecuteReflexivePipeCorrectlyManyElements() {
		int numElements = 1_000;
		ManyElementsWithStatelessStageGlobalTaskPoolConfig config = new ManyElementsWithStatelessStageGlobalTaskPoolConfig(numElements);
		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(NUM_THREADS, config, 1);
		Execution<ManyElementsWithStatelessStageGlobalTaskPoolConfig> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		List<Integer> processedElements = config.getSink().getElements();
		for (int i = 0; i < numElements; i++) {
			assertThat(processedElements.get(i), is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
