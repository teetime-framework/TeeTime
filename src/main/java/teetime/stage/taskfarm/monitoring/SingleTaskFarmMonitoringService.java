package teetime.stage.taskfarm.monitoring;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

public class SingleTaskFarmMonitoringService implements IMonitoringService<TaskFarmStage<?, ?, ?>, TaskFarmMonitoringData> {

	private static final long INIT = -1;

	private long startingTimestamp = INIT;

	private final List<TaskFarmMonitoringData> monitoredDatas = new LinkedList<TaskFarmMonitoringData>();
	private final TaskFarmStage<?, ?, ?> taskFarmStage;

	private int maxNumberOfStages = 0;

	public SingleTaskFarmMonitoringService(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	@Override
	public List<TaskFarmMonitoringData> getData() {
		return this.monitoredDatas;
	}

	@Override
	public void addMonitoredItem(final TaskFarmStage<?, ?, ?> taskFarmStage) {
		throw new IllegalStateException("SingleTaskFarmMonitoringService can only monitor the one Task Farm given to the constructor.");
	}

	@Override
	public void addMonitoringData() {
		long currentTimestamp = System.currentTimeMillis();
		if (this.startingTimestamp == INIT) {
			this.startingTimestamp = currentTimestamp;
		}

		TaskFarmMonitoringData monitoringData = new TaskFarmMonitoringData(currentTimestamp - this.startingTimestamp,
				taskFarmStage.getEnclosedStageInstances().size(),
				getMeanThroughput(taskFarmStage, MeanThroughputType.PULL),
				getMeanThroughput(taskFarmStage, MeanThroughputType.PUSH),
				taskFarmStage.getConfiguration().getThroughputScoreBoundary());

		monitoredDatas.add(monitoringData);

		if (taskFarmStage.getEnclosedStageInstances().size() > this.maxNumberOfStages) {
			this.maxNumberOfStages = taskFarmStage.getEnclosedStageInstances().size();
		}
	}

	public int getMaxNumberOfStages() {
		return maxNumberOfStages;
	}

	private enum MeanThroughputType {
		PUSH, PULL
	}

	private double getMeanThroughput(final TaskFarmStage<?, ?, ?> taskFarmStage, final MeanThroughputType type) {
		double sum = 0;
		double count = 0;

		try {
			for (ITaskFarmDuplicable<?, ?> enclosedStage : taskFarmStage.getEnclosedStageInstances()) {
				IMonitorablePipe inputPipe = (IMonitorablePipe) enclosedStage.getInputPort().getPipe();
				if (inputPipe != null) {
					switch (type) {
					case PULL:
						sum += inputPipe.getPullThroughput();
						break;
					case PUSH:
						sum += inputPipe.getPushThroughput();
						break;
					default:
						break;
					}

					count++;
				}
			}
		} catch (ClassCastException e) {
			throw new TaskFarmInvalidPipeException(
					"The input pipe of an enclosed stage instance inside a Task Farm"
							+ " does not implement IMonitorablePipe, which is required.");
		}

		if (count > 0) {
			sum /= count;
		}

		return sum;
	}
}
