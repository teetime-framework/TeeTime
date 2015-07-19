package teetime.stage.taskfarm.adaptation.history;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

public class TaskFarmHistoryService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmStage<I, O, T> taskFarmStage;
	private final ThroughputHistory history = new ThroughputHistory();

	public TaskFarmHistoryService(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	public ThroughputHistory getHistory() {
		return history;
	}

	public void monitorPipes() {
		double sum = 0;
		double count = 0;

		try {
			for (ITaskFarmDuplicable<I, O> enclosedStage : taskFarmStage.getEnclosedStageInstances()) {
				IMonitorablePipe inputPipe = (IMonitorablePipe) enclosedStage.getInputPort().getPipe();
				if (inputPipe != null) {
					sum += inputPipe.getPushThroughput();
					count++;
				}
			}
		} catch (ClassCastException e) {
			throw new TaskFarmInvalidPipeException(
					"The input pipe of an enclosed stage instance inside a Task Farm"
							+ " does not implement IMonitorablePipe, which is required.");
		}

		// count is never 0, since every Task Farm has at least the basic enclosed stage
		history.add(sum / count);
	}
}
