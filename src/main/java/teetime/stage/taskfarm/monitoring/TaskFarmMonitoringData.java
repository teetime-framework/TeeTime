package teetime.stage.taskfarm.monitoring;

public class TaskFarmMonitoringData implements IMonitoringData {

	private final int stages;

	TaskFarmMonitoringData(final int stages) {
		this.stages = stages;
	}

	public int getStages() {
		return stages;
	}
}
