package teetime.stage.taskfarm.adaptation;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.TaskFarmAnalyzer;
import teetime.stage.taskfarm.adaptation.history.TaskFarmHistoryService;
import teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationService;

class TaskFarmComponents<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmHistoryService<I, O, T> historyService;
	private final TaskFarmAnalyzer<I, O, T> analysisService;
	private final TaskFarmReconfigurationService<I, O, T> reconfigurationService;
	private final TaskFarmStage<I, O, T> taskFarmStage;

	public TaskFarmComponents(final TaskFarmStage<I, O, T> taskFarmStage) {
		historyService = new TaskFarmHistoryService<I, O, T>(taskFarmStage);
		analysisService = new TaskFarmAnalyzer<I, O, T>(taskFarmStage.getConfiguration());
		reconfigurationService = new TaskFarmReconfigurationService<I, O, T>(taskFarmStage);
		this.taskFarmStage = taskFarmStage;
	}

	public void executeServices() {
		historyService.monitorPipes();
		analysisService.analyze(historyService.getHistory());
		reconfigurationService.reconfigure(analysisService.getThroughputScore());
	}

	public TaskFarmStage<I, O, T> getTaskFarmStage() {
		return taskFarmStage;
	}

}
