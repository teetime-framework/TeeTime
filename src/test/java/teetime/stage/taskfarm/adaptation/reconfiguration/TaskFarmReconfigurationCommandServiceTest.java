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
package teetime.stage.taskfarm.adaptation.reconfiguration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.DynamicTaskFarmStage;
import teetime.stage.taskfarm.ITaskFarmDuplicable;

public class TaskFarmReconfigurationCommandServiceTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@Ignore
	public void executionPlanTestA() {
		DynamicTaskFarmStage taskFarmStage = createTaskFarm();
		TaskFarmReconfigurationCommandService commandService = new TaskFarmReconfigurationCommandService(taskFarmStage);

		assertThat(commandService.decideExecutionPlan(0.5d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		for (int i = 0; i < taskFarmStage.getConfiguration().getMaxSamplesUntilRemove(); i++) {
			assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		}
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.REMOVE)));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@Ignore
	public void executionPlanTestB() {
		DynamicTaskFarmStage taskFarmStage = createTaskFarm();
		TaskFarmReconfigurationCommandService commandService = new TaskFarmReconfigurationCommandService(taskFarmStage);

		assertThat(commandService.decideExecutionPlan(0.5d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.3d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		assertThat(commandService.decideExecutionPlan(0.3d), is(equalTo(TaskFarmReconfigurationCommand.ADD)));
		for (int i = 0; i < taskFarmStage.getConfiguration().getMaxSamplesUntilRemove(); i++) {
			assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.NONE)));
		}
		assertThat(commandService.decideExecutionPlan(0.1d), is(equalTo(TaskFarmReconfigurationCommand.REMOVE)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DynamicTaskFarmStage createTaskFarm() {
		DynamicTaskFarmStage taskFarmStage = new DynamicTaskFarmStage(new DummyDuplicableStage(), 1);
		return taskFarmStage;
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
