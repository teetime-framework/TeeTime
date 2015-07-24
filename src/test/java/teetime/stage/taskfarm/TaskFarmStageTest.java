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

import org.junit.Test;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.Execution;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.basic.AbstractTransformation;

public class TaskFarmStageTest {

	static final int NUMBER_OF_TEST_ELEMENTS = 10000;

	@Test
	public void simpleTaskFarmStageTest() {
		final TaskFarmStageConfiguration configuration = new TaskFarmStageConfiguration(this);
		final Execution<TaskFarmStageConfiguration> execution = new Execution<TaskFarmStageConfiguration>(configuration);

		execution.executeBlocking();

		// assertThat(configuration.getCollection().size(), is(NUMBER_OF_TEST_ELEMENTS));
	}

	static private class PlusOneInStringStage extends AbstractTransformation<Integer, String> {

		@Override
		protected void execute(final Integer element) {
			final Integer x = element + 1;
			this.outputPort.send(x.toString());
		}
	}

	static class StringDuplicationStage extends AbstractFilter<String> implements ITaskFarmDuplicable<String, String> {

		@Override
		protected void execute(final String element) {
			this.outputPort.send(element + element);
		}

		@Override
		public ITaskFarmDuplicable<String, String> duplicate() {
			return new StringDuplicationStage();
		}
	}

	static class CompositeTestStage extends AbstractCompositeStage implements ITaskFarmDuplicable<Integer, String> {
		private final PlusOneInStringStage pOne = new PlusOneInStringStage();
		private final StringDuplicationStage sDup = new StringDuplicationStage();

		public CompositeTestStage() {
			this(false);
		}

		public CompositeTestStage(final boolean runtime) {
			super();
			if (runtime) {
				SingleElementPipeFactory factory = new SingleElementPipeFactory();
				factory.create(this.pOne.getOutputPort(), this.sDup.getInputPort());
			} else {
				connectPorts(this.pOne.getOutputPort(), this.sDup.getInputPort());
			}
		}

		@Override
		public InputPort<Integer> getInputPort() {
			return this.pOne.getInputPort();
		}

		@Override
		public OutputPort<String> getOutputPort() {
			return this.sDup.getOutputPort();
		}

		@Override
		public ITaskFarmDuplicable<Integer, String> duplicate() {
			return new CompositeTestStage(true);
		}
	}
}
