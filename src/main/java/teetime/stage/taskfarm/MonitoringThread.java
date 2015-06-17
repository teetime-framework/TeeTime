package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.stage.taskfarm.analysis.TaskFarmScheduler;
import teetime.stage.taskfarm.execution.TaskFarmController;

final public class MonitoringThread extends Thread {

	private final static int sampleRate = 200;

	private class ScheduledTaskFarm {
		private final TaskFarmStage<?, ?> taskFarmStage;
		private final TaskFarmScheduler scheduler;
		private final TaskFarmController controller;

		public ScheduledTaskFarm(final TaskFarmStage<?, ?> taskFarmStage, final TaskFarmScheduler scheduler, final TaskFarmController controller) {
			this.taskFarmStage = taskFarmStage;
			this.scheduler = scheduler;
			this.controller = controller;
		}
	}

	private final List<ScheduledTaskFarm> monitoredTaskFarms = new LinkedList<ScheduledTaskFarm>();

	protected void addTaskFarm(final TaskFarmStage<?, ?> taskFarmStage) {
		TaskFarmScheduler scheduler = new TaskFarmScheduler();
		TaskFarmController controller = new TaskFarmController();
		this.monitoredTaskFarms.add(new ScheduledTaskFarm(taskFarmStage, scheduler, controller));
	}

}
