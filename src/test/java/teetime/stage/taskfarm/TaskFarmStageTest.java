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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.Execution;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.basic.AbstractTransformation;

public class TaskFarmStageTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskFarmStageTest.class);
	private static final int NUMBER_OF_TEST_ELEMENTS = 10000;

	volatile static int numPlusOneInString;
	volatile static int numStringDuplication;

	@Test
	public void simpleTaskFarmStageTest() throws InterruptedException {
		final TaskFarmStageConfiguration configuration = new TaskFarmStageConfiguration(NUMBER_OF_TEST_ELEMENTS);
		final Execution<TaskFarmStageConfiguration> execution = new Execution<TaskFarmStageConfiguration>(configuration);

		execution.executeBlocking();
		LOGGER.debug("FINISHED TEST");

		assertThat(numPlusOneInString, is(NUMBER_OF_TEST_ELEMENTS));
		assertThat(numStringDuplication, is(NUMBER_OF_TEST_ELEMENTS));
		assertThat(configuration.getCollection().size(), is(NUMBER_OF_TEST_ELEMENTS));
	}

	static private class PlusOneInStringStage extends AbstractTransformation<Integer, String> {

		@Override
		protected void execute(final Integer element) {
			final Integer x = element + 1;
			numPlusOneInString++;
			this.outputPort.send(x.toString());
		}
	}

	static class StringDuplicationStage extends AbstractFilter<String> implements ITaskFarmDuplicable<String, String> {

		@Override
		protected void execute(final String element) {
			numStringDuplication++;
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
