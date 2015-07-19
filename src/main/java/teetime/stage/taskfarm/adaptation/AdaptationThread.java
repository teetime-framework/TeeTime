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
import teetime.stage.taskfarm.exception.TaskFarmAdaptationThreadException;

final public class AdaptationThread extends Thread {

	private static int sampleRateMillis = 50;

	private final List<TaskFarmComponents<?, ?, ?>> taskFarmServices = new LinkedList<TaskFarmComponents<?, ?, ?>>();

	private boolean stopping = false;

	@Override
	public void run() {
		while (!stopping) {
			try {
				Thread.sleep(sampleRateMillis);
			} catch (InterruptedException e) {
				throw new TaskFarmAdaptationThreadException("AdaptationThread was interrupted!");
			}

			executeNextStageToBeReconfigured();
		}
	}

	public <I, O, T extends ITaskFarmDuplicable<I, O>> void addTaskFarm(final TaskFarmStage<I, O, T> taskFarmStage) {
		TaskFarmComponents<I, O, T> service = new TaskFarmComponents<I, O, T>(taskFarmStage);
		synchronized (taskFarmServices) {
			taskFarmServices.add(service);
		}
	}

	public static void setSampleRate(final int sampleRateMillis) {
		AdaptationThread.sampleRateMillis = sampleRateMillis;
	}

	private void executeNextStageToBeReconfigured() {
		synchronized (taskFarmServices) {
			for (TaskFarmComponents<?, ?, ?> service : taskFarmServices) {
				// execute first Task Farm which is still parallelizable
				if (service.getTaskFarmStage().getConfiguration().isStillParallelizable()) {
					service.executeServices();
					break;
				}
			}
		}
	}

	public void stopAdaptationThread() {
		stopping = true;
	}
}
