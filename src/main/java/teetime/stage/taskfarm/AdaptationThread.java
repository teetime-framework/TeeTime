package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.taskfarm.analysis.TaskFarmAnalyzer;
import teetime.stage.taskfarm.execution.TaskFarmController;

final public class AdaptationThread extends Thread {

	private final static int sampleRate = 200;

	private class ScheduledTaskFarm {
		private final TaskFarmStage<?, ?, ?> taskFarmStage;
		private final TaskFarmAnalyzer analyzer;
		private final TaskFarmController controller;

		public ScheduledTaskFarm(final TaskFarmStage<?, ?, ?> taskFarmStage, final TaskFarmAnalyzer analyzer, final TaskFarmController controller) {
			this.taskFarmStage = taskFarmStage;
			this.analyzer = analyzer;
			this.controller = controller;
		}
	}

	private final List<ScheduledTaskFarm> monitoredTaskFarms = new LinkedList<ScheduledTaskFarm>();

	protected void addTaskFarm(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		TaskFarmAnalyzer analyzer = new TaskFarmAnalyzer();
		TaskFarmController controller = new TaskFarmController();
		this.monitoredTaskFarms.add(new ScheduledTaskFarm(taskFarmStage, analyzer, controller));
	}

}
