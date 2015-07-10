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
package teetime.stage.taskfarm.adaptation;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalyzer;
import teetime.stage.taskfarm.adaptation.execution.TaskFarmController;

final public class AdaptationThread extends Thread {

	private final static int sampleRate = 200;

	private class ScheduledTaskFarm {
		private final TaskFarmStage<?, ?, ?> taskFarmStage;
		private final TaskFarmAnalyzer analyzer;
		private final TaskFarmController<?, ?, ?> controller;

		public ScheduledTaskFarm(
				final TaskFarmStage<?, ?, ?> taskFarmStage,
				final TaskFarmAnalyzer analyzer,
				final TaskFarmController<?, ?, ?> controller) {
			this.taskFarmStage = taskFarmStage;
			this.analyzer = analyzer;
			this.controller = controller;
		}
	}

	private final List<ScheduledTaskFarm> monitoredTaskFarms = new LinkedList<ScheduledTaskFarm>();

	protected <I, O, TFS extends ITaskFarmDuplicable<I, O>> void addTaskFarm(final TaskFarmStage<I, O, TFS> taskFarmStage) {
		TaskFarmAnalyzer analyzer = new TaskFarmAnalyzer();
		TaskFarmController<I, O, TFS> controller = new TaskFarmController<I, O, TFS>(taskFarmStage);
		this.monitoredTaskFarms.add(new ScheduledTaskFarm(taskFarmStage, analyzer, controller));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
}
