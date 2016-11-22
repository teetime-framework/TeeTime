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
package teetime.stage.taskfarm.adaptation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalysisService;
import teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationService;

/**
 * Represents the adaptation thread used implement the self-adaptive behavior of the task farm.
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
final public class AdaptationThread<I, O, T extends ITaskFarmDuplicable<I, O>> extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdaptationThread.class);

	private volatile boolean shouldTerminate;

	/** task farm of this adaptation thread **/
	private final TaskFarmStage<I, O, T> taskFarmStage;

	// services of this adaptation thread (history, analysis, reconfiguration)
	private final TaskFarmHistoryService<I, O, T> historyService;
	private final TaskFarmAnalysisService<I, O, T> analysisService;
	private final TaskFarmReconfigurationService<I, O, T> reconfigurationService;

	/**
	 * Creates an adaptation thread for the given task farm.
	 *
	 * @param taskFarmStage
	 *            given task farm instance
	 */
	public AdaptationThread(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.historyService = new TaskFarmHistoryService<I, O, T>(taskFarmStage);
		this.analysisService = new TaskFarmAnalysisService<I, O, T>(taskFarmStage.getConfiguration());
		this.reconfigurationService = new TaskFarmReconfigurationService<I, O, T>(taskFarmStage);
		this.taskFarmStage = taskFarmStage;

		this.setPriority(MAX_PRIORITY);
	}

	/**
	 * Start the execution of the adaptation thread. The execution should happen after
	 * the start of the merger of the corresponding task farm.
	 */
	@Override
	public void run() {
		LOGGER.debug("Adaptation thread started");
		while (!this.shouldTerminate) {
			try {
				executeServices();
				doMonitoring();

				Thread.sleep(taskFarmStage.getConfiguration().getAdaptationWaitingTimeMillis());
			} catch (InterruptedException e) {
				this.shouldTerminate = true;
			}
		}
		LOGGER.debug("Adaptation thread stopped");
	}

	private void doMonitoring() {
		if (this.taskFarmStage.getConfiguration().isMonitoringEnabled()) {
			this.taskFarmStage.getPipeMonitoringService().doMeasurement();
			this.taskFarmStage.getTaskFarmMonitoringService().doMeasurement();
		}
	}

	private void executeServices() throws InterruptedException {
		this.historyService.monitorPipes();
		this.analysisService.analyze(this.historyService.getHistory());
		this.reconfigurationService.reconfigure(this.analysisService.getThroughputScore());
	}

	/**
	 * Terminate the adaptation thread. The termination should happen after
	 * the termination of the merger of the corresponding task farm.
	 */
	public void stopAdaptationThread() {
		this.shouldTerminate = true;
		interrupt();
		LOGGER.debug("Adaptation thread stop signal sent");
	}

	/**
	 * Returns the {@link teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService TaskFarmHistoryService} of this adaptation thread, containing pipe
	 * throughput measurements.
	 *
	 * @return {@link teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService TaskFarmHistoryService} of this adaptation thread
	 */
	public TaskFarmHistoryService<I, O, T> getHistoryService() {
		return this.historyService;
	}
}
