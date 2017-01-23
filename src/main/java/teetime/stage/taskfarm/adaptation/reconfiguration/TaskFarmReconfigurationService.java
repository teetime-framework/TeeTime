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

import teetime.stage.taskfarm.DynamicTaskFarmStage;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalysisService;

/**
 * Represents a service to start the reconfiguration process for the task farm
 * using a particular throughput score. Should be called after the {@link TaskFarmAnalysisService}.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of the parallelized stage
 */
public class TaskFarmReconfigurationService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final DynamicTaskFarmStage<I, O, T> taskFarmStage;
	/** corresponding command service which includes the decision tree **/
	private final TaskFarmReconfigurationCommandService<I, O, T> reconfigurationCommandService;

	/**
	 * Create a task farm reconfiguration service for a specified task farm.
	 *
	 * @param taskFarmStage
	 *            specified task farm
	 */
	public TaskFarmReconfigurationService(final DynamicTaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		this.reconfigurationCommandService = new TaskFarmReconfigurationCommandService<I, O, T>(taskFarmStage);
	}

	/**
	 * Starts the reconfiguration process of the corresponding task farm for the specified throughput score.
	 *
	 * @param throughputScore
	 *            specified throughput score
	 * @throws InterruptedException
	 */
	public void reconfigure(final double throughputScore) throws InterruptedException {
		TaskFarmReconfigurationCommand command = this.reconfigurationCommandService.decideExecutionPlan(throughputScore);

		switch (command) {
		case ADD:
			taskFarmStage.addStageAtRuntime();
			break;
		case REMOVE:
			taskFarmStage.removeStageAtRuntime();
			break;
		case NONE:
		default:
			break;
		}
	}

}
