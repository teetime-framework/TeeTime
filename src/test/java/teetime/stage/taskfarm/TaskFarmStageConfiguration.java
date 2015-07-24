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

import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.taskfarm.TaskFarmStageTest.CompositeTestStage;

class TaskFarmStageConfiguration extends Configuration {

	private final int numberOfTestElements;
	private final List<String> results = new LinkedList<String>();

	public TaskFarmStageConfiguration(final int numberOfTestElements) {
		this.numberOfTestElements = numberOfTestElements;
		this.buildConfiguration();
	}

	private void buildConfiguration() {
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
}
