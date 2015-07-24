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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;

final public class AdaptationThread extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdaptationThread.class);

	private volatile static int sampleRateMillis = 50;

	private final List<TaskFarmComponents<?, ?, ?>> taskFarmServices = new LinkedList<TaskFarmComponents<?, ?, ?>>();

	private volatile boolean stopped = false;

	@Override
	public void run() {
		LOGGER.debug("Adaptation thread started");
		while (!stopped) {
			try {
				Thread.sleep(sampleRateMillis);

				executeNextStageToBeReconfigured();
				checkForStopping();
			} catch (InterruptedException e) {
				stopped = true;
			}
		}
		LOGGER.debug("Adaptation thread stopped");
	}

	public <I, O, T extends ITaskFarmDuplicable<I, O>> void addTaskFarm(final TaskFarmStage<I, O, T> taskFarmStage) {
		TaskFarmComponents<I, O, T> service = new TaskFarmComponents<I, O, T>(taskFarmStage);
		taskFarmServices.add(service);
	}

	public static void setSampleRate(final int sampleRateMillis) {
		AdaptationThread.sampleRateMillis = sampleRateMillis;
	}

	private void executeNextStageToBeReconfigured() throws InterruptedException {
		for (TaskFarmComponents<?, ?, ?> service : taskFarmServices) {
			// execute first Task Farm which is still parallelizable
			if (service.getTaskFarmStage().getConfiguration().isStillParallelizable()) {
				service.executeServices();
				break;
			}
		}
	}

	private void checkForStopping() {
		boolean parallelizableStageRemaining = false;

		// checks if there is still a parallelizable Task Farm
		for (TaskFarmComponents<?, ?, ?> service : taskFarmServices) {
			if (service.getTaskFarmStage().getConfiguration().isStillParallelizable()) {
				parallelizableStageRemaining = true;
			}
		}

		if (!parallelizableStageRemaining) {
			stopAdaptationThread();
		}
	}

	public void stopAdaptationThread() {
		LOGGER.debug("Adaptation thread stop signal sent");
		stopped = true;
		interrupt();
	}
}
