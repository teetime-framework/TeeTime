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
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.AbstractFilter;
import teetime.stage.basic.AbstractTransformation;

class TaskFarmStageTestConfiguration extends Configuration {

	static volatile int counter;

	private final List<String> results = new LinkedList<String>();

	public TaskFarmStageTestConfiguration(final int numberOfTestElements) {
		this.buildConfiguration(numberOfTestElements);
	}

	private void buildConfiguration(final int numberOfTestElements) {
		final List<Integer> values = new LinkedList<Integer>();
		for (int i = 1; i <= numberOfTestElements; i++) {
			values.add(i);
		}
		final InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(values);
		final CompositeTestStage compositeTestStage = new CompositeTestStage();
		final TaskFarmStage<Integer, String, CompositeTestStage> taskFarmStage =
				new TaskFarmStage<Integer, String, CompositeTestStage>(compositeTestStage);
		final CollectorSink<String> collectorSink = new CollectorSink<String>(this.results);

		connectPorts(initialElementProducer.getOutputPort(), taskFarmStage.getInputPort());
		connectPorts(taskFarmStage.getOutputPort(), collectorSink.getInputPort());
	}

	public List<String> getCollection() {
		return this.results;
	}

	private static class CompositeTestStage extends AbstractCompositeStage implements ITaskFarmDuplicable<Integer, String> {
		private final PlusOneInStringStage pOne = new PlusOneInStringStage();
		private final StringDuplicationStage sDup = new StringDuplicationStage();

		public CompositeTestStage() {
			connectPorts(this.pOne.getOutputPort(), this.sDup.getInputPort());
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
			return new CompositeTestStage();
		}
	}

	private static class PlusOneInStringStage extends AbstractTransformation<Integer, String> {

		@Override
		protected void execute(final Integer element) {
			final Integer x = element + 1;
			counter++;
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

}
