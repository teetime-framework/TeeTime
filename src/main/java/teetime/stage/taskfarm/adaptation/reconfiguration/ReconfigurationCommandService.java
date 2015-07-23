package teetime.stage.taskfarm.adaptation.reconfiguration;

import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;

class ReconfigurationCommandService<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmConfiguration<I, O, T> configuration;
	private int samplesUntilRemove;

	ReconfigurationCommandService(final TaskFarmConfiguration<I, O, T> configuration) {
		this.configuration = configuration;
		this.samplesUntilRemove = TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE;
	}

	public TaskFarmReconfigurationCommand decideExecutionPlan(final double throughputScore) {
		TaskFarmReconfigurationCommand command = TaskFarmReconfigurationCommand.NONE;

		if (samplesUntilRemove == TaskFarmConfiguration.INIT_SAMPLES_UNTIL_REMOVE) {
			// new execution, start adding stages
			samplesUntilRemove = configuration.getMaxSamplesUntilRemove();
			command = TaskFarmReconfigurationCommand.ADD;
		} else {
			if (samplesUntilRemove > 0) {
				// we still have to wait before removing a new stage again

				if (throughputScore > configuration.getThroughputScoreBoundary()) {
					// we could find a performance increase, add another stage
					samplesUntilRemove = configuration.getMaxSamplesUntilRemove();
					command = TaskFarmReconfigurationCommand.ADD;
				} else {
					// we did not find a performance increase, wait a bit longer
					samplesUntilRemove--;
					command = TaskFarmReconfigurationCommand.NONE;
				}
			} else {
				// we found a boundary where new stages will not increase performance
				configuration.setStillParallelizable(false);
				command = TaskFarmReconfigurationCommand.REMOVE;
			}
		}

		return command;
	}
}
