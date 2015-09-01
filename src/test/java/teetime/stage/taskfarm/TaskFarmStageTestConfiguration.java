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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.Configuration;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.basic.AbstractTransformation;
import teetime.stage.basic.Sink;

public class TaskFarmStageTestConfiguration extends Configuration {

	private static volatile int numOfElements = 0;
	private TaskFarmStage<Long, String, CompositeTestStage> taskFarmStage;

	public TaskFarmStageTestConfiguration(final long numberOfTestElements) {
		this.buildConfiguration(numberOfTestElements);
	}

	private void buildConfiguration(final long numberOfTestElements) {
		final List<Long> values = new LinkedList<Long>();
		for (long i = 1; i <= numberOfTestElements; i++) {
			values.add(i);
		}
		final InitialElementProducer<Long> initialElementProducer = new InitialElementProducer<Long>(values);
		final CompositeTestStage compositeTestStage = new CompositeTestStage();
		taskFarmStage = new TaskFarmStage<Long, String, CompositeTestStage>(compositeTestStage, 1000);
		taskFarmStage.getConfiguration().setThroughputScoreBoundary(0.2);
		taskFarmStage.getConfiguration().setThroughputAlgorithm("RegressionAlgorithm");
		taskFarmStage.getConfiguration().setAdaptationWaitingTimeMillis(50);
		taskFarmStage.getConfiguration().setMonitoringEnabled(true);
		Sink<String> sink = new Sink<String>();

		connectPorts(initialElementProducer.getOutputPort(), taskFarmStage.getInputPort());
		connectPorts(taskFarmStage.getOutputPort(), sink.getInputPort());
	}

	private static class CompositeTestStage extends AbstractCompositeStage implements ITaskFarmDuplicable<Long, String> {
		private final PlusOneInStringStage pOne = new PlusOneInStringStage();
		private final StringDuplicationStage sDup = new StringDuplicationStage();

		public CompositeTestStage() {
			connectPorts(this.pOne.getOutputPort(), this.sDup.getInputPort());
		}

		@Override
		public InputPort<Long> getInputPort() {
			return this.pOne.getInputPort();
		}

		@Override
		public OutputPort<String> getOutputPort() {
			return this.sDup.getOutputPort();
		}

		@Override
		public ITaskFarmDuplicable<Long, String> duplicate() {
			return new CompositeTestStage();
		}
	}

	private static class PlusOneInStringStage extends AbstractTransformation<Long, String> {

		@Override
		protected void execute(final Long element) {
			final Long x = element + 1;
			synchronized (TaskFarmStageTestConfiguration.class) {
				numOfElements++;
			}
			this.outputPort.send(x.toString());
		}
	}

	private static class StringDuplicationStage extends AbstractFilter<String> implements ITaskFarmDuplicable<String, String> {

		@Override
		protected void execute(final String element) {
			this.outputPort.send(element + element);
		}

		@Override
		public ITaskFarmDuplicable<String, String> duplicate() {
			return new StringDuplicationStage();
		}
	}

	public static int getNumOfElements() {
		synchronized (TaskFarmStageTestConfiguration.class) {
			return numOfElements;
		}
	}

	public TaskFarmStage<Long, String, CompositeTestStage> getTaskFarmStage() {
		return taskFarmStage;
	}
}
