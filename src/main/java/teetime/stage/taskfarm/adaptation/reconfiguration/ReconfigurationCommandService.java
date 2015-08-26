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

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.AbstractThroughputAnalysisAlgorithm;

class ReconfigurationCommandService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmStage<I, O, T> taskFarmStage;
	private int samplesUntilRemove;

	ReconfigurationCommandService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		this.samplesUntilRemove = TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE;
	}

	public TaskFarmReconfigurationCommand decideExecutionPlan(final double throughputScore) {
		TaskFarmReconfigurationCommand command = TaskFarmReconfigurationCommand.NONE;

		if (taskFarmStage.getEnclosedStageInstances().size() >= taskFarmStage.getConfiguration().getMaxNumberOfCores()) {
			// we do not want to parallelize more than we have (virtual) processors
			taskFarmStage.getConfiguration().setStillParallelizable(false);
			command = TaskFarmReconfigurationCommand.NONE;
		} else {
			if (throughputScore != AbstractThroughputAnalysisAlgorithm.INVALID_SCORE) {
				if (samplesUntilRemove == TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE) {
					// new execution, start adding stages
					samplesUntilRemove = taskFarmStage.getConfiguration().getMaxSamplesUntilRemove();
					command = TaskFarmReconfigurationCommand.ADD;
				} else {
					if (samplesUntilRemove > 0) {
						// we still have to wait before removing a new stage again

						if (throughputScore > taskFarmStage.getConfiguration().getThroughputScoreBoundary()) {
							// we could find a performance increase, add another stage
							samplesUntilRemove = taskFarmStage.getConfiguration().getMaxSamplesUntilRemove();
							command = TaskFarmReconfigurationCommand.ADD;
						} else {
							// we did not find a performance increase, wait a bit longer
							samplesUntilRemove--;
							command = TaskFarmReconfigurationCommand.NONE;
						}
					} else {
						// we found a boundary where new stages will not increase performance
						taskFarmStage.getConfiguration().setStillParallelizable(false);
						command = TaskFarmReconfigurationCommand.REMOVE;
					}
				}
			}
		}

		return command;
	}
}
