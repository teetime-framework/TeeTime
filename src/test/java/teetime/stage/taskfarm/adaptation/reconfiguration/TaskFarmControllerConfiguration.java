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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmControllerTest.SelfMonitoringPlusOneStage;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmControllerTest.TaskFarmControllerControllerStage;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * @author Christian Claus Wiechmann
 */
class TaskFarmControllerConfiguration extends Configuration {

	private final ListMultimap<Integer, Integer> monitoredValues;

	public TaskFarmControllerConfiguration() {
		final ListMultimap<Integer, Integer> multimapNotSynchronized = LinkedListMultimap.create();
		this.monitoredValues = Multimaps.synchronizedListMultimap(multimapNotSynchronized);
		this.buildConfiguration();
	}

	private void buildConfiguration() {
		final List<Integer> numbers = new LinkedList<Integer>();
		for (int i = 0; i < TaskFarmControllerTest.NUMBER_OF_ITEMS; i++) {
			numbers.add(i);
		}

		final InitialElementProducer<Integer> initialElementProducer;
		final TaskFarmStage<Integer, Integer, SelfMonitoringPlusOneStage> taskFarmStage;
		final TaskFarmController<Integer, Integer, SelfMonitoringPlusOneStage> controller;

		initialElementProducer = new InitialElementProducer<Integer>(numbers);

		final SelfMonitoringPlusOneStage workerStage = new SelfMonitoringPlusOneStage(this.monitoredValues);
		taskFarmStage = new TaskFarmStage<Integer, Integer, SelfMonitoringPlusOneStage>(workerStage);

		controller = new TaskFarmController<Integer, Integer, SelfMonitoringPlusOneStage>(taskFarmStage);
		final TaskFarmControllerControllerStage taskFarmControllerControllerStage = new TaskFarmControllerControllerStage(controller);

		final Sink<Integer> sink = new Sink<Integer>();

		connectPorts(initialElementProducer.getOutputPort(), taskFarmControllerControllerStage.getInputPort());
		connectPorts(taskFarmControllerControllerStage.getOutputPort(), taskFarmStage.getInputPort());
		connectPorts(taskFarmStage.getOutputPort(), sink.getInputPort());
	}

	public ListMultimap<Integer, Integer> getMonitoredValues() {
		return this.monitoredValues;
	}
}
