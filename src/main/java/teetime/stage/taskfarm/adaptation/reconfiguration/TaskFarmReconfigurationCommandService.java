/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.AbstractThroughputAlgorithm;

/**
 * Represents the decision tree which decides if a worker stage is to be
 * added to or removed from a task farm.
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
class TaskFarmReconfigurationCommandService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	/** corresponding task farm to be reconfigured **/
	private final TaskFarmStage<I, O, T> taskFarmStage;
	/** remaining time for the performance increase to occur before stage removal **/
	private int samplesUntilRemove;
	/** current reconfiguration mode, at the beginning we want to add stages **/
	private ReconfigurationMode currentMode = ReconfigurationMode.ADDING;

	/**
	 * Creates a task farm reconfiguration command service for the specified task farm.
	 *
	 * @param taskFarmStage
	 *            specified task farm
	 */
	TaskFarmReconfigurationCommandService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		this.samplesUntilRemove = TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE;
	}

	/**
	 * Decides if we want to add or remove a stage using a specified throughput score.
	 *
	 * @param throughputScore
	 *            specified throughput score
	 * @return {@link TaskFarmReconfigurationCommand} showing if we want to add or remove a stage
	 */
	public TaskFarmReconfigurationCommand decideExecutionPlan(final double throughputScore) {
		TaskFarmReconfigurationCommand command = TaskFarmReconfigurationCommand.NONE; // NOPMD

		if (this.currentMode == ReconfigurationMode.ADDING) {
			command = decideForAddingMode(throughputScore);
		} else {
			command = decideForRemovingMode(throughputScore);
		}

		return command;
	}

	private TaskFarmReconfigurationCommand decideForAddingMode(final double throughputScore) {
		TaskFarmReconfigurationCommand command = TaskFarmReconfigurationCommand.NONE; // NOPMD

		if (this.taskFarmStage.getEnclosedStageInstances().size() >= this.taskFarmStage.getConfiguration().getMaxNumberOfCores()) {
			// we do not want to parallelize more than we have (virtual) processors
			this.currentMode = ReconfigurationMode.REMOVING;
			command = TaskFarmReconfigurationCommand.NONE;
		} else {
			if (throughputScore != AbstractThroughputAlgorithm.INVALID_SCORE) {
				if (this.samplesUntilRemove == TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE) {
					// new execution, start adding stages
					this.samplesUntilRemove = taskFarmStage.getConfiguration().getMaxSamplesUntilRemove();
					command = TaskFarmReconfigurationCommand.ADD;
				} else {
					if (this.samplesUntilRemove > 0) {
						// we still have to wait before removing a new stage again

						if (throughputScore > this.taskFarmStage.getConfiguration().getThroughputScoreBoundary()) {
							// we could find a performance increase, add another stage
							samplesUntilRemove = this.taskFarmStage.getConfiguration().getMaxSamplesUntilRemove();
							command = TaskFarmReconfigurationCommand.ADD;
						} else {
							// we did not find a performance increase, wait a bit longer
							this.samplesUntilRemove--;
							command = TaskFarmReconfigurationCommand.NONE;
						}
					} else {
						// we found a boundary where new stages will not increase performance
						this.currentMode = ReconfigurationMode.REMOVING;
						command = TaskFarmReconfigurationCommand.REMOVE;
					}
				}
			}
		}

		return command;
	}

	private TaskFarmReconfigurationCommand decideForRemovingMode(final double throughputScore) {
		TaskFarmReconfigurationCommand command = TaskFarmReconfigurationCommand.NONE; // NOPMD

		// we never want to remove the basic stage since it would destroy the pipeline
		for (int i = 1; i < this.taskFarmStage.getEnclosedStageInstances().size() - 1; i++) {
			ITaskFarmDuplicable<?, ?> stage = this.taskFarmStage.getEnclosedStageInstances().get(i);

			IMonitorablePipe monitorableInputPipe = (IMonitorablePipe) stage.getInputPort().getPipe();
			int sizeOfInputQueue = monitorableInputPipe.size();

			if (sizeOfInputQueue == 0) {
				// there is still a stage which is currently unused can be safely removed
				command = TaskFarmReconfigurationCommand.REMOVE;
				break;
			}
		}

		if (throughputScore > this.taskFarmStage.getConfiguration().getThroughputScoreBoundary()) {
			// performance need has risen again, so we are parallelizing more
			this.currentMode = ReconfigurationMode.ADDING;
		}

		return command;
	}

	private enum ReconfigurationMode {
		/** add new worker stages as long as the throughput is increased because of it **/
		ADDING,
		/** remove unused stages to reduce overhead **/
		REMOVING
	}
}
