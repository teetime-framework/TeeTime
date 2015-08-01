/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.framework.Execution;

public class TaskFarmStageTest {

	// private static final Logger LOGGER = LoggerFactory.getLogger(TaskFarmStageTest.class);

	private static final int NUMBER_OF_TEST_ELEMENTS = 10000;

	@Test
	public void simpleTaskFarmStageTest() throws InterruptedException {
		final TaskFarmStageTestConfiguration configuration = new TaskFarmStageTestConfiguration(NUMBER_OF_TEST_ELEMENTS);
		final Execution<TaskFarmStageTestConfiguration> execution = new Execution<TaskFarmStageTestConfiguration>(configuration);

		execution.executeBlocking();

		assertThat(configuration.getCollection().size(), is(NUMBER_OF_TEST_ELEMENTS));
	}

}
