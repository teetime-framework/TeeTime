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
package teetime.stage.taskfarm.adaptation.reconfiguration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmStage;

public class ReconfigurationCommandServiceTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void executionPlanTestA() {
		TaskFarmConfiguration configuration = createConfiguration();
		ReconfigurationCommandService commandService = new ReconfigurationCommandService(configuration);

		assertTrue(configuration.isStillParallelizable());
		assertThat(commandService.decideExecutionPlan(0.5d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		for (int i = 0; i < configuration.getMaxSamplesUntilRemove(); i++) {
			assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		}
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.REMOVE)));
		assertFalse(configuration.isStillParallelizable());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void executionPlanTestB() {
		TaskFarmConfiguration configuration = createConfiguration();
		ReconfigurationCommandService commandService = new ReconfigurationCommandService(configuration);

		assertTrue(configuration.isStillParallelizable());
		assertThat(commandService.decideExecutionPlan(0.5d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertTrue(configuration.isStillParallelizable());
		assertThat(commandService.decideExecutionPlan(0.3d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertTrue(configuration.isStillParallelizable());
		assertThat(commandService.decideExecutionPlan(0.3d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		for (int i = 0; i < configuration.getMaxSamplesUntilRemove(); i++) {
			assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		}
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.REMOVE)));
		assertFalse(configuration.isStillParallelizable());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TaskFarmConfiguration createConfiguration() {
		TaskFarmStage taskFarmStage = new TaskFarmStage(new DummyDuplicableStage());
		TaskFarmConfiguration configuration = taskFarmStage.getConfiguration();
		return configuration;
	}

	@SuppressWarnings("rawtypes")
	private class DummyDuplicableStage extends AbstractFilter implements ITaskFarmDuplicable {
		@Override
		public ITaskFarmDuplicable duplicate() {
			return null;
		}

		@Override
		protected void execute(final Object element) {
			// nothing to do
		}
	}
}
