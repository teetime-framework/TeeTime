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
package teetime.stage.taskfarm.adaptation.history;

import java.util.HashMap;
import java.util.Map;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.DynamicTaskFarmStage;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

/**
 * Represents the history service which provides the other
 * adaptation thread services with performance measurements.
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
public class TaskFarmHistoryService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	/** corresponding task farm **/
	private final DynamicTaskFarmStage<I, O, T> taskFarmStage;
	/** measurement container **/
	private final ThroughputHistory history;

	// contains push/pull throughput of each pipe for the previous measurement
	private Map<IMonitorablePipe, Long> lastPushThroughputs;
	private Map<IMonitorablePipe, Long> lastPullThroughputs;

	/**
	 * Create a task farm history service for the specified task farm.
	 *
	 * @param taskFarmStage
	 *            specified task farm
	 */
	public TaskFarmHistoryService(final DynamicTaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		this.history = new ThroughputHistory(taskFarmStage.getConfiguration());
	}

	/**
	 * @return the measurement container of this history service
	 */
	public ThroughputHistory getHistory() {
		return this.history;
	}

	/**
	 * Records another throughput sum measurement of all pipes in the task farm of this service.
	 */
	public void monitorPipes() {
		this.history.add(getSumOfPipePushThroughputs());
	}

	private double getSumOfPipePushThroughputs() {
		this.lastPullThroughputs = new HashMap<>();
		this.lastPushThroughputs = new HashMap<>();
		double sum = 0; // NOPMD

		try {
			for (ITaskFarmDuplicable<I, O> enclosedStage : this.taskFarmStage.getWorkerStages()) {
				IMonitorablePipe inputPipe = (IMonitorablePipe) enclosedStage.getInputPort().getPipe();
				if (inputPipe != null) {
					// we record the throughput measurements as a sum in the history
					// and separately in maps for further use
					long pushThroughput = inputPipe.getPushThroughput();
					this.lastPushThroughputs.put(inputPipe, pushThroughput);
					long pullThroughput = inputPipe.getPullThroughput();
					this.lastPullThroughputs.put(inputPipe, pullThroughput);
					sum += pullThroughput;
				}
			}
		} catch (ClassCastException e) {
			throw new TaskFarmInvalidPipeException(
					"The input pipe of an enclosed stage instance inside a Task Farm"
							+ " does not implement IMonitorablePipe, which is required.",
					e);
		}

		return sum;
	}

	/**
	 * @param pipe
	 *            specified monitorable pipe
	 * @return last pull throughput measurement of specified pipe
	 *         (zero if no throughput value for the pipe has been recorded at the last measurement)
	 */
	public long getLastPullThroughputOfPipe(final IMonitorablePipe pipe) {
		long result = 0; // NOPMD
		if (this.lastPullThroughputs.containsKey(pipe)) {
			result = this.lastPullThroughputs.get(pipe);
		}
		return result;
	}

	/**
	 * @param pipe
	 *            specified monitorable pipe
	 * @return last push throughput measurement of specified pipe
	 *         (zero if no throughput value for the pipe has been recorded at the last measurement)
	 */
	public long getLastPushThroughputOfPipe(final IMonitorablePipe pipe) {
		long result = 0; // NOPMD
		if (this.lastPushThroughputs.containsKey(pipe)) {
			result = this.lastPushThroughputs.get(pipe);
		}
		return result;
	}
}
