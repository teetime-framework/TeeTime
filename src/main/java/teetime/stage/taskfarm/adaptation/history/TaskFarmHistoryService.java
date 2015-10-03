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
package teetime.stage.taskfarm.adaptation.history;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

public class TaskFarmHistoryService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmStage<I, O, T> taskFarmStage;
	private final ThroughputHistory history;

	private Map<IMonitorablePipe, Long> lastPushThroughputs;
	private Map<IMonitorablePipe, Long> lastPullThroughputs;

	public TaskFarmHistoryService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		history = new ThroughputHistory(taskFarmStage.getConfiguration());
	}

	public ThroughputHistory getHistory() {
		return history;
	}

	public void monitorPipes() {
		history.add(getSumOfPipePushThroughputs());
	}

	private double getSumOfPipePushThroughputs() {
		lastPullThroughputs = new HashMap<IMonitorablePipe, Long>();
		lastPushThroughputs = new HashMap<IMonitorablePipe, Long>();
		double sum = 0;

		try {
			for (ITaskFarmDuplicable<I, O> enclosedStage : taskFarmStage.getEnclosedStageInstances()) {
				IMonitorablePipe inputPipe = (IMonitorablePipe) enclosedStage.getInputPort().getPipe();
				if (inputPipe != null) {
					long pushThroughput = inputPipe.getPushThroughput();
					lastPushThroughputs.put(inputPipe, pushThroughput);
					long pullThroughput = inputPipe.getPullThroughput();
					lastPullThroughputs.put(inputPipe, pullThroughput);
					sum += pullThroughput;
				}
			}
		} catch (ClassCastException e) {
			throw new TaskFarmInvalidPipeException(
					"The input pipe of an enclosed stage instance inside a Task Farm"
							+ " does not implement IMonitorablePipe, which is required.");
		}

		return sum;
	}

	public long getLastPullThroughputOfPipe(final IMonitorablePipe pipe) {
		long result = 0;
		if (lastPullThroughputs.containsKey(pipe)) {
			result = lastPullThroughputs.get(pipe);
		}
		return result;
	}

	public long getLastPushThroughputOfPipe(final IMonitorablePipe pipe) {
		long result = 0;
		if (lastPushThroughputs.containsKey(pipe)) {
			result = lastPushThroughputs.get(pipe);
		}
		return result;
	}
}
