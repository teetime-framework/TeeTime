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
package teetime.stage.taskfarm.monitoring;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

public class SingleTaskFarmMonitoringService implements IMonitoringService<TaskFarmStage<?, ?, ?>, TaskFarmMonitoringData> {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;

	private final List<TaskFarmMonitoringData> monitoredDatas = new LinkedList<TaskFarmMonitoringData>();
	private final TaskFarmStage<?, ?, ?> taskFarmStage;

	private int maxNumberOfStages = 0;

	public SingleTaskFarmMonitoringService(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	@Override
	public List<TaskFarmMonitoringData> getData() {
		return this.monitoredDatas;
	}

	@Override
	public void addMonitoredItem(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		throw new IllegalStateException("SingleTaskFarmMonitoringService can only monitor the one Task Farm given to the constructor.");
	}

	@Override
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		TaskFarmMonitoringData monitoringData = new TaskFarmMonitoringData(currentTimestamp - this.startingTimestamp,
				taskFarmStage.getEnclosedStageInstances().size(),
				getMeanAndSumThroughput(taskFarmStage, MeanThroughputType.PULL, true),
				getMeanAndSumThroughput(taskFarmStage, MeanThroughputType.PUSH, true),
				getMeanAndSumThroughput(taskFarmStage, MeanThroughputType.PULL, false),
				getMeanAndSumThroughput(taskFarmStage, MeanThroughputType.PUSH, false),
				taskFarmStage.getConfiguration().getThroughputScoreBoundary());

		monitoredDatas.add(monitoringData);

		if (taskFarmStage.getEnclosedStageInstances().size() > this.maxNumberOfStages) {
			this.maxNumberOfStages = taskFarmStage.getEnclosedStageInstances().size();
		}
	}

	public int getMaxNumberOfStages() {
		return maxNumberOfStages;
	}

	private enum MeanThroughputType {
		PUSH, PULL
	}

	private double getMeanAndSumThroughput(final TaskFarmStage<?, ?, ?> taskFarmStage, final MeanThroughputType type, final boolean mean) {
		double sum = 0;
		double count = 0;

		try {
			for (ITaskFarmDuplicable<?, ?> enclosedStage : taskFarmStage.getEnclosedStageInstances()) {
				IMonitorablePipe inputPipe = (IMonitorablePipe) enclosedStage.getInputPort().getPipe();
				if (inputPipe != null) {
					switch (type) {
					case PULL:
						sum += inputPipe.getPullThroughput();
						break;
					case PUSH:
						sum += inputPipe.getPushThroughput();
						break;
					default:
						break;
					}

					count++;
				}
			}
		} catch (ClassCastException e) {
			throw new TaskFarmInvalidPipeException(
					"The input pipe of an enclosed stage instance inside a Task Farm"
							+ " does not implement IMonitorablePipe, which is required.");
		}

		if (mean) {
			if (count > 0) {
				sum /= count;
			}
		}

		return sum;
	}
}
