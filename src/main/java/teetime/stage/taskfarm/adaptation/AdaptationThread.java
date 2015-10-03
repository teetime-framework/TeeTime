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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalyzer;
import teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationService;

final public class AdaptationThread<I, O, T extends ITaskFarmDuplicable<I, O>> extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdaptationThread.class);

	private volatile boolean shouldTerminate;

	private final TaskFarmStage<I, O, T> taskFarmStage;
	private final TaskFarmHistoryService<I, O, T> historyService;
	private final TaskFarmAnalyzer<I, O, T> analysisService;
	private final TaskFarmReconfigurationService<I, O, T> reconfigurationService;

	public AdaptationThread(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.historyService = new TaskFarmHistoryService<I, O, T>(taskFarmStage);
		this.analysisService = new TaskFarmAnalyzer<I, O, T>(taskFarmStage.getConfiguration());
		this.reconfigurationService = new TaskFarmReconfigurationService<I, O, T>(taskFarmStage);
		this.taskFarmStage = taskFarmStage;

		this.setPriority(MAX_PRIORITY);
	}

	@Override
	public void run() {
		LOGGER.debug("Adaptation thread started");
		while (!shouldTerminate) {
			try {
				executeServices();
				doMonitoring();

				Thread.sleep(taskFarmStage.getConfiguration().getAdaptationWaitingTimeMillis());
			} catch (InterruptedException e) {
				shouldTerminate = true;
			}
		}
		LOGGER.debug("Adaptation thread stopped");
	}

	private void doMonitoring() {
		if (taskFarmStage.getConfiguration().isMonitoringEnabled()) {
			taskFarmStage.getPipeMonitoringService().addMonitoringData();
			taskFarmStage.getTaskFarmMonitoringService().addMonitoringData();
		}
	}

	private void executeServices() throws InterruptedException {
		historyService.monitorPipes();
		analysisService.analyze(historyService.getHistory());
		reconfigurationService.reconfigure(analysisService.getThroughputScore());
	}

	public void stopAdaptationThread() {
		shouldTerminate = true;
		interrupt();
		LOGGER.debug("Adaptation thread stop signal sent");
	}

	public TaskFarmHistoryService<I, O, T> getHistoryService() {
		return historyService;
	}
}
