/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
