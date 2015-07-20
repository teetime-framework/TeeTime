package teetime.stage.taskfarm.adaptation.reconfiguration;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;

public class TaskFarmReconfigurationService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final ReconfigurationCommandService<I, O, T> reconfigurationCommandService;
	private final TaskFarmController<I, O> controller;

	public TaskFarmReconfigurationService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.reconfigurationCommandService = new ReconfigurationCommandService<I, O, T>(taskFarmStage.getConfiguration());
		this.controller = new TaskFarmController<I, O>(taskFarmStage);
	}

	public void reconfigure(final double throughputScore) {
		TaskFarmReconfigurationCommand command = reconfigurationCommandService.decideExecutionPlan(throughputScore);

		switch (command) {
		case ADD:
			controller.addStageToTaskFarm();
			break;
		case REMOVE:
			controller.removeStageFromTaskFarm();
			break;
		case NONE:
		default:
			break;
		}
	}

}
