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

import teetime.stage.taskfarm.DynamicTaskFarmStage;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalysisService;
import teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationService;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

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
public class AdaptationThread<I, O, T extends ITaskFarmDuplicable<I, O>> extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdaptationThread.class);

	private volatile boolean shouldTerminate;

	/** task farm of this adaptation thread **/
	private final TaskFarmConfiguration<I, O, T> taskFarmStageConfiguration;

	// services of this adaptation thread (history, analysis, reconfiguration)
	private final TaskFarmHistoryService<I, O, T> historyService;
	private final TaskFarmAnalysisService<I, O, T> analysisService;
	private final TaskFarmReconfigurationService<I, O, T> reconfigurationService;

	private final SingleTaskFarmMonitoringService taskFarmMonitoringService;
	private final PipeMonitoringService pipeMonitoringService;

	/**
	 * Creates an adaptation thread for the given task farm.
	 *
	 * @param taskFarmStage
	 *            given task farm instance
	 */
	public AdaptationThread(final DynamicTaskFarmStage<I, O, T> taskFarmStage) {
		this.historyService = new TaskFarmHistoryService<>(taskFarmStage);
		this.analysisService = new TaskFarmAnalysisService<>(taskFarmStage.getConfiguration());
		this.reconfigurationService = new TaskFarmReconfigurationService<>(taskFarmStage);
		this.taskFarmStageConfiguration = taskFarmStage.getConfiguration();

		this.taskFarmMonitoringService = new SingleTaskFarmMonitoringService(taskFarmStage, historyService);
		this.pipeMonitoringService = new PipeMonitoringService(historyService);

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

				Thread.sleep(taskFarmStageConfiguration.getAdaptationWaitingTimeMillis());
			} catch (InterruptedException e) {
				this.shouldTerminate = true;
			}
		}
		LOGGER.debug("Adaptation thread stopped");
	}

	private void doMonitoring() {
		if (this.taskFarmStageConfiguration.isMonitoringEnabled()) {
			this.pipeMonitoringService.doMeasurement();
			this.taskFarmMonitoringService.doMeasurement();
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

	public PipeMonitoringService getPipeMonitoringService() {
		return pipeMonitoringService;
	}

	public SingleTaskFarmMonitoringService getTaskFarmMonitoringService() {
		return taskFarmMonitoringService;
	}
}
