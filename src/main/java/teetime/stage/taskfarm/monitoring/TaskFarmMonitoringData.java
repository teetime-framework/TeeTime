/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

/**
 * Represents all parameters that are recorded per measurement for a task farm.
 *
 * @author Christian Claus Wiechmann
 */
public class TaskFarmMonitoringData implements IMonitoringData {

	/** time of measurement **/
	private final long time;
	/** current number of worker stages **/
	private final int stages;
	/** current mean pull throughput of all pipes between distributor and a worker stage **/
	private final double meanPullThroughput;
	/** current mean push throughput of all pipes between distributor and a worker stage **/
	private final double meanPushThroughput;
	/** current total pull throughput of all pipes between distributor and a worker stage **/
	private final double sumOfPullThroughput;
	/** current total push throughput of all pipes between distributor and a worker stage **/
	private final double sumOfPushThroughput;
	/** current throughput boundary **/
	private final double throughputBoundary;

	/**
	 * Constructor.
	 * 
	 * @param time
	 *            time of measurement
	 * @param stages
	 *            current number of worker stages
	 * @param meanPullThroughput
	 *            current mean pull throughput of all pipes between distributor and a worker stage
	 * @param meanPushThroughput
	 *            current mean push throughput of all pipes between distributor and a worker stage
	 * @param sumOfPullThroughput
	 *            current total pull throughput of all pipes between distributor and a worker stage
	 * @param sumOfPushThroughput
	 *            current total push throughput of all pipes between distributor and a worker stage
	 * @param throughputBoundary
	 *            current throughput boundary
	 */
	TaskFarmMonitoringData(final long time, final int stages, final double meanPullThroughput, final double meanPushThroughput, final double sumOfPullThroughput,
			final double sumOfPushThroughput, final double throughputBoundary) {
		super();
		this.time = time;
		this.stages = stages;
		this.meanPullThroughput = meanPullThroughput;
		this.meanPushThroughput = meanPushThroughput;
		this.throughputBoundary = throughputBoundary;
		this.sumOfPullThroughput = sumOfPullThroughput;
		this.sumOfPushThroughput = sumOfPushThroughput;
	}

	/**
	 * @return time of measurement
	 */
	public long getTime() {
		return this.time;
	}

	/**
	 * @return current number of worker stages
	 */
	public int getStages() {
		return this.stages;
	}

	/**
	 * @return current mean pull throughput of all pipes between distributor and a worker stage
	 */
	public double getMeanPullThroughput() {
		return this.meanPullThroughput;
	}

	/**
	 * @return current mean push throughput of all pipes between distributor and a worker stage
	 */
	public double getMeanPushThroughput() {
		return this.meanPushThroughput;
	}

	/**
	 * @return current total pull throughput of all pipes between distributor and a worker stage
	 */
	public double getThroughputBoundary() {
		return this.throughputBoundary;
	}

	/**
	 * @return current total push throughput of all pipes between distributor and a worker stage
	 */
	public double getSumOfPullThroughput() {
		return this.sumOfPullThroughput;
	}

	/**
	 * @return current throughput boundary
	 */
	public double getSumOfPushThroughput() {
		return this.sumOfPushThroughput;
	}
}
