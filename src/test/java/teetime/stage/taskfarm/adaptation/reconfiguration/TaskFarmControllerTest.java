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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;

import com.google.common.collect.ListMultimap;

/**
 * @author Christian Claus Wiechmann
 */
public class TaskFarmControllerTest {

	static final int NUMBER_OF_ITEMS = 10000;

	private static int numberOfEnclosedStage = 0;

	@Test
	public void test() {
		final TaskFarmControllerConfiguration configuration = new TaskFarmControllerConfiguration();
		final Execution<TaskFarmControllerConfiguration> execution = new Execution<TaskFarmControllerConfiguration>(configuration);

		execution.executeBlocking();

		final ListMultimap<Integer, Integer> monitoredValues = configuration.getMonitoredValues();
		assertThat(monitoredValues.get(0).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(1).size(), is(greaterThan(0)));
		assertThat(monitoredValues.get(2).size(), is(greaterThan(0)));
		assertThat(monitoredValues.size(), is(equalTo(NUMBER_OF_ITEMS)));
	}

	/**
	 * @author Christian Claus Wiechmann
	 */
	static class SelfMonitoringPlusOneStage extends AbstractFilter<Integer> implements ITaskFarmDuplicable<Integer, Integer> {

		private final ListMultimap<Integer, Integer> monitoredValues;
		private final int stageId;

		public SelfMonitoringPlusOneStage(final ListMultimap<Integer, Integer> monitoredValues) {
			this.monitoredValues = monitoredValues;
			this.stageId = numberOfEnclosedStage;
			numberOfEnclosedStage++;
		}

		@Override
		protected void execute(final Integer element) {
			this.monitoredValues.put(this.stageId, element);
			final Integer x = element + 1;
			this.outputPort.send(x);
		}

		@Override
		public ITaskFarmDuplicable<Integer, Integer> duplicate() {
			return new SelfMonitoringPlusOneStage(monitoredValues);
		}
	}

	/**
	 * @author Christian Claus Wiechmann
	 */
	static class TaskFarmControllerControllerStage extends AbstractFilter<Integer> {

		private final TaskFarmController<?, ?> controller;
		private int numberOfElements = 0;

		public TaskFarmControllerControllerStage(final TaskFarmController<?, ?> controller) {
			this.controller = controller;
		}

		@Override
		protected void execute(final Integer element) {
			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.3) {
				this.controller.addStageToTaskFarm();
			}

			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.5) {
				this.controller.addStageToTaskFarm();
			}

			if (this.numberOfElements == NUMBER_OF_ITEMS * 0.7) {
				this.controller.removeStageFromTaskFarm();
			}

			this.outputPort.send(element);

			this.numberOfElements++;
		}

	}

}
