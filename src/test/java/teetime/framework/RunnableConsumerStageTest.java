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
package teetime.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.Thread.State;

import org.junit.Test;

public class RunnableConsumerStageTest {

	@Test
	public void testWaitingInfinitely() throws Exception {
		final RunnableConsumerStageTestConfig configuration = new RunnableConsumerStageTestConfig(0, 1, 2, 3, 5);

		assertThat(configuration.getCollectorSink().getCurrentState(), is(StageState.CREATED));
		assertThat(configuration.getConsumerThread(), is(nullValue()));

		// starts the threads, but does not send the start signal
		new Execution<RunnableConsumerStageTestConfig>(configuration);
		assertThat(configuration.getCollectorSink().getCurrentState(), is(StageState.VALIDATED));

		Thread.sleep(200);

		assertThat(configuration.getCollectorSink().getCurrentState(), is(StageState.VALIDATED)); // still validated
		assertThat(configuration.getConsumerThread().getState(), is(State.WAITING));
		assertThat(configuration.getCollectedElements().size(), is(0));
	}

	@Test
	public void testCorrectStartAndTerminatation() throws Exception {
		RunnableConsumerStageTestConfig configuration = new RunnableConsumerStageTestConfig(0, 1, 2, 3, 5);

		final Execution<?> execution = new Execution<RunnableConsumerStageTestConfig>(configuration);
		execution.executeBlocking();

		assertThat(configuration.getCollectedElements().size(), is(5));
	}

}
