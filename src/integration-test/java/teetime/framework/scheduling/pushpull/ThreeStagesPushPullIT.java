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
package teetime.framework.scheduling.pushpull;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.TeeTimeScheduler;
import teetime.framework.scheduling.pushpullmodel.PushPullScheduling;
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.StreamProducer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThreeStagesPushPullIT {

	private static final int NUM_ELEMENTS = 1_000_000;

	@Test
	public void shouldExecuteWith01Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1);
	}

	@Test
	public void shouldExecuteWith02Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2);
	}

	@Test
	public void shouldExecuteWith04Thread() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4);
	}

	private void shouldExecutePipelineCorrectlyManyElements(final int numElements, final int numThreads) {
		List<Integer> processedElements = new ArrayList<>();

		IntStream inputElements = IntStream.iterate(0, i -> i + 1).limit(numElements);
		Configuration config = new Configuration()
				.from(new StreamProducer<>(inputElements))
				.to(new Counter<>())
				.end(new CollectorSink<>(processedElements));

		TeeTimeScheduler scheduling = new PushPullScheduling(config);
		Execution<Configuration> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			Integer actualElement = processedElements.get(i);
			assertThat(actualElement, is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
