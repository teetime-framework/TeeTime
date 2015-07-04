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
		private final TaskFarmController<?, ?, ?> controller;

		public ScheduledTaskFarm(
				final TaskFarmStage<?, ?, ?> taskFarmStage,
				final TaskFarmAnalyzer analyzer,
				final TaskFarmController<?, ?, ?> controller) {
			this.taskFarmStage = taskFarmStage;
			this.analyzer = analyzer;
			this.controller = controller;
		}
	}

	private final List<ScheduledTaskFarm> monitoredTaskFarms = new LinkedList<ScheduledTaskFarm>();

	protected <I, O, TFS extends ITaskFarmDuplicable<I, O>> void addTaskFarm(final TaskFarmStage<I, O, TFS> taskFarmStage) {
		TaskFarmAnalyzer analyzer = new TaskFarmAnalyzer();
		TaskFarmController<I, O, TFS> controller = new TaskFarmController<I, O, TFS>(taskFarmStage.getConfiguration());
		this.monitoredTaskFarms.add(new ScheduledTaskFarm(taskFarmStage, analyzer, controller));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
}
