package teetime.stage.taskfarm.monitoring;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teetime.stage.taskfarm.TaskFarmStage;

public class TaskFarmMonitoringService implements IMonitoringService<TaskFarmStage<?, ?, ?>, TaskFarmMonitoringData> {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;

	private final Map<TaskFarmStage<?, ?, ?>, List<TaskFarmMonitoringData>> data = new HashMap<TaskFarmStage<?, ?, ?>, List<TaskFarmMonitoringData>>();

	@Override
	public Map<TaskFarmStage<?, ?, ?>, List<TaskFarmMonitoringData>> getData() {
		return this.data;
	}

	@Override
	public void addMonitoredItem(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		if (!this.data.containsKey(taskFarmStage)) {
			this.data.put(taskFarmStage, new LinkedList<TaskFarmMonitoringData>());
		}
	}

	@Override
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		for (TaskFarmStage<?, ?, ?> taskFarmStage : this.data.keySet()) {
			TaskFarmMonitoringData monitoringData = new TaskFarmMonitoringData(taskFarmStage.getEnclosedStageInstances().size());

			List<TaskFarmMonitoringData> taskFarmValues = this.data.get(taskFarmStage);
			taskFarmValues.add(monitoringData);
		}
	}
}
