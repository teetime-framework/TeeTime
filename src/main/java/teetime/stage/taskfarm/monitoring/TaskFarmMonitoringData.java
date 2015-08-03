package teetime.stage.taskfarm.monitoring;

public class TaskFarmMonitoringData implements IMonitoringData {

	private final long time;
	private final int stages;
	private final double meanPullThroughput;
	private final double meanPushThroughput;
	private final double throughputBoundary;

	TaskFarmMonitoringData(final long time, final int stages, final double meanPullThroughput, final double meanPushThroughput, final double throughputBoundary) {
		super();
		this.time = time;
		this.stages = stages;
		this.meanPullThroughput = meanPullThroughput;
		this.meanPushThroughput = meanPushThroughput;
		this.throughputBoundary = throughputBoundary;
	}

	public long getTime() {
		return time;
	}

	public int getStages() {
		return stages;
	}

	public double getMeanPullThroughput() {
		return meanPullThroughput;
	}

	public double getMeanPushThroughput() {
		return meanPushThroughput;
	}

	public double getThroughputBoundary() {
		return throughputBoundary;
	}
}
