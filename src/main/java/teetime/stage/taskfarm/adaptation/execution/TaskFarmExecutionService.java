package teetime.stage.taskfarm.adaptation.execution;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;

public class TaskFarmExecutionService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmStage<I, O, T> taskFarmStage;
	private final ExecutionCommandService<I, O, T> executionCommandService;
	private final TaskFarmController<I, O, T> controller;

	public TaskFarmExecutionService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
		this.executionCommandService = new ExecutionCommandService<I, O, T>(taskFarmStage.getConfiguration());
		this.controller = new TaskFarmController<I, O, T>(taskFarmStage);
	}

	public void execute(final double throughputScore) {

		TaskFarmExecutionCommand command = executionCommandService.decideExecutionPlan(throughputScore);

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
