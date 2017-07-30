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
package teetime.framework.scheduling.globaltaskqueue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.ConfigurationContext;
import teetime.framework.Execution;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;
import teetime.stage.*;
import teetime.util.ConstructorClosure;

public class PipelineTest {

	private static class GlobalTaskQueueConfig<T> extends Configuration {

		private static final int NUM_THREADS = 4;
		private static final GlobalTaskQueueScheduling SCHEDULER = new GlobalTaskQueueScheduling(NUM_THREADS);
		private CollectorSink<T> sink;

		public GlobalTaskQueueConfig(final T... elements) {
			super(new TerminatingExceptionListenerFactory(), new ConfigurationContext(SCHEDULER));
			SCHEDULER.setConfiguration(this);
			build(elements);
		}

		private void build(final T... elements) {
			InitialElementProducer<T> producer = new InitialElementProducer<>(elements);
			Counter<T> counter = new Counter<>();
			// StatelessCounter<T> counter = new StatelessCounter<>();
			NoopFilter<T> noopFilter = new NoopFilter<>();
			sink = new CollectorSink<>();
			from(producer).to(counter).to(noopFilter).end(sink);
		}

		public CollectorSink<T> getSink() {
			return sink;
		}
	}

	private static class ManyElementsGlobalTaskQueueConfig extends Configuration {

		private static final int NUM_THREADS = 4;
		private static final GlobalTaskQueueScheduling SCHEDULER = new GlobalTaskQueueScheduling(NUM_THREADS);
		private CollectorSink<Integer> sink;

		public ManyElementsGlobalTaskQueueConfig(final int numInputObjects) {
			super(new TerminatingExceptionListenerFactory(), new ConfigurationContext(SCHEDULER));
			SCHEDULER.setConfiguration(this);
			build(numInputObjects);
		}

		private void build(final int numInputObjects) {
			ObjectProducer<Integer> producer = new ObjectProducer<>(numInputObjects, new ConstructorClosure<Integer>() {
				private int counter;

				@Override
				public Integer create() {
					return counter++;
				}
			});
			Counter<Integer> counter = new Counter<>();
			// StatelessCounter<T> counter = new StatelessCounter<>();
			// NoopFilter<Integer> noopFilter = new NoopFilter<>();
			sink = new CollectorSink<>();
			from(producer).to(counter).end(sink);
			// from(producer).to(counter).to(noopFilter).end(sink);
		}

		public CollectorSink<Integer> getSink() {
			return sink;
		}
	}

	@Test
	public void shouldExecutePipelineCorrectlyFewElements() throws Exception {
		String[] inputElements = { "a", "b", "c" };
		GlobalTaskQueueConfig<String> config = new GlobalTaskQueueConfig<>(inputElements);
		Execution<GlobalTaskQueueConfig<String>> execution = new Execution<>(config);
		execution.executeBlocking();

		List<String> processedElements = config.getSink().getElements();
		List<String> expectedElements = Arrays.asList("a", "b", "c");
		// assertThat(processedElements.get(0), is(nullValue()));
		// assertThat(processedElements.get(0), is("null"));
		// assertThat(processedElements.get(0), is(equalTo("a")));
		assertThat(processedElements, is(equalTo(expectedElements)));
	}

	@Test
	public void shouldExecutePipelineCorrectlyManyElements() throws Exception {
		int numElements = 1_000;
		ManyElementsGlobalTaskQueueConfig config = new ManyElementsGlobalTaskQueueConfig(numElements);
		Execution<ManyElementsGlobalTaskQueueConfig> execution = new Execution<>(config);
		execution.executeBlocking();

		List<Integer> processedElements = config.getSink().getElements();
		// for (int i = 0; i < numElements; i++) {
		// assertThat(processedElements.get(i), is(i));
		// }
		assertThat(processedElements, hasSize(numElements));
	}
}
