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
import teetime.stage.CollectorSink;
import teetime.stage.Counter;
import teetime.stage.StreamProducer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThreeStagesGlobalTaskPoolIT {

	private static final int NUM_ELEMENTS = 1_000_000;

	@Test
	public void shouldExecuteWith01Thread0001Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 4);
	}

	@Test
	public void shouldExecuteWith01Thread0002Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 2);
	}

	@Test
	public void shouldExecuteWith01Thread0003Executions() { // with an odd number
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 3);
	}

	@Test
	public void shouldExecuteWith01Thread0004Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 4);
	}

	@Test
	public void shouldExecuteWith01Thread0016Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 16);
	}

	@Test
	public void shouldExecuteWith01Thread0031Executions() { // with a prime number
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 31);
	}

	@Test
	public void shouldExecuteWith01Thread0256Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 256);
	}

	@Test
	public void shouldExecuteWith01Thread0128Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 128);
	}

	@Test
	public void shouldExecuteWith01Thread1024Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 1, 1024);
	}

	@Test
	public void shouldExecuteWith02Thread0001Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 1);
	}

	@Test
	public void shouldExecuteWith02Thread0002Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 2);
	}

	@Test
	public void shouldExecuteWith02Thread0003Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 3);
	}

	@Test
	public void shouldExecuteWith02Thread0031Executions() { // with a prime number
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 31);
	}

	@Test
	public void shouldExecuteWith02Thread0256Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 256);
	}

	@Test
	public void shouldExecuteWith02Thread1024Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 2, 1024);
	}

	@Test
	public void shouldExecuteWith04Thread0001Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 1);
	}

	@Test
	public void shouldExecuteWith04Thread0002Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 2);
	}

	@Test
	public void shouldExecuteWith04Thread0003Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 3);
	}

	@Test
	public void shouldExecuteWith04Thread0004Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 4);
	}

	@Test
	public void shouldExecuteWith04Thread0031Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 31);
	}

	@Test
	public void shouldExecuteWith04Thread064Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 64);
	}

	@Test
	public void shouldExecuteWith04Thread0128Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 128);
	}

	@Test
	public void shouldExecuteWith04Thread0256Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 256);
	}

	@Test
	public void shouldExecuteWith04Thread1024Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 4, 1024);
	}

	@Test
	public void shouldExecuteWith08Thread1024Executions() {
		shouldExecutePipelineCorrectlyManyElements(NUM_ELEMENTS, 8, 31);
	}

	private void shouldExecutePipelineCorrectlyManyElements(final int numElements, final int numThreads, final int numExecutions) {
		List<Integer> processedElements = new ArrayList<>();

		IntStream inputElements = IntStream.iterate(0, i -> i + 1).limit(numElements);
		Configuration config = new Configuration()
				.from(new StreamProducer<>(inputElements))
				.to(new Counter<>())
				.end(new CollectorSink<>(processedElements));

		TeeTimeScheduler scheduling = new GlobalTaskPoolScheduling(numThreads, config, numExecutions);
		Execution<Configuration> execution = new Execution<>(config, true, scheduling);
		execution.executeBlocking();

		for (int i = 0; i < numElements; i++) {
			Integer actualElement = processedElements.get(i);
			assertThat(actualElement, is(i));
		}
		assertThat(processedElements, hasSize(numElements));
	}
}
