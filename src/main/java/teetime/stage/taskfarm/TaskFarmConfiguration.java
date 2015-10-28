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
package teetime.stage.taskfarm;

import teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm.WeightMethod;

/**
 * Represents the configuration of a single Task Farm.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmConfiguration<I, O, T extends ITaskFarmDuplicable<I, O>> {

	/**
	 * represents an initial value for the samples until remove for {@link teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationCommandService
	 * TaskFarmReconfigurationCommandService}
	 **/
	public static final int INIT_SAMPLES_UNTIL_REMOVE = -1;

	/** should the monitoring services be activated (does not affect the adaptation thread!)? **/
	private volatile boolean monitoringEnabled = false;

	/** the waiting time between each iteration of the adaptation thread **/
	private volatile int adaptationWaitingTimeMillis = 50;

	/** the amount of previous measurements used by the throughput algorithm **/
	private volatile int analysisWindow = 5;
	/** used throughput algorithm (has to exist in the package <code>teetime.stage.taskfarm.adaptation.analysis.algorithm</code>) **/
	private volatile String throughputAlgorithm = "RegressionAlgorithm";
	/**
	 * if the {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm WeightedAlgorithm} is used as the throughput algorithm, this
	 * {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm.WeightMethod WeightMethod} is used
	 **/
	private volatile WeightMethod weightedAlgorithmMethod = WeightMethod.EXPONENTIAL;
	/**
	 * the {@link teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationCommandService
	 * TaskFarmReconfigurationCommandService} waits this amount of adaptation thread iterations for performance improvements after a new worker stage is added
	 **/
	private volatile int maxSamplesUntilRemove = 5;
	/** throughput boundary of this task farm **/
	private volatile double throughputScoreBoundary = 0.2d;

	/** pipe capacity of all pipes inside the task farm **/
	private volatile int pipeCapacity = 100;

	/** the maximum number of worker stages the task farm may have **/
	private volatile int maxNumberOfCores = Runtime.getRuntime().availableProcessors() - 2;

	TaskFarmConfiguration() {}

	/**
	 *
	 * @return the amount of previous measurements used by the throughput algorithm
	 */
	public int getAnalysisWindow() {
		return this.analysisWindow;
	}

	/**
	 *
	 * @param analysisWindow
	 *            the amount of previous measurements used by the throughput algorithm
	 */
	public void setAnalysisWindow(final int analysisWindow) {
		this.analysisWindow = analysisWindow;
	}

	/**
	 *
	 * @return used throughput algorithm (has to exist in the package <code>teetime.stage.taskfarm.adaptation.analysis.algorithm</code>)
	 */
	public String getThroughputAlgorithm() {
		return throughputAlgorithm;
	}

	/**
	 *
	 * @param throughputAlgorithm
	 *            used throughput algorithm (has to exist in the package <code>teetime.stage.taskfarm.adaptation.analysis.algorithm</code>)
	 */
	public void setThroughputAlgorithm(final String throughputAlgorithm) {
		this.throughputAlgorithm = throughputAlgorithm;
	}

	/**
	 *
	 * @return if the {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm WeightedAlgorithm} is used as the throughput algorithm, this
	 *         {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm.WeightMethod WeightMethod} is used
	 */
	public WeightMethod getWeightedAlgorithmMethod() {
		return weightedAlgorithmMethod;
	}

	/**
	 *
	 * @param weightedAlgorithmMethod
	 *            if the {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm WeightedAlgorithm} is used as the throughput algorithm, this
	 *            {@link teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm.WeightMethod WeightMethod} is used
	 */
	public void setWeightedAlgorithmMethod(final WeightMethod weightedAlgorithmMethod) {
		this.weightedAlgorithmMethod = weightedAlgorithmMethod;
	}

	/**
	 *
	 * @return the {@link teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationCommandService
	 *         TaskFarmReconfigurationCommandService} waits this amount of adaptation thread iterations for performance improvements after a new worker stage is
	 *         added
	 */
	public int getMaxSamplesUntilRemove() {
		return maxSamplesUntilRemove;
	}

	/**
	 *
	 * @param maxSamplesUntilRemove
	 *            the {@link teetime.stage.taskfarm.adaptation.reconfiguration.TaskFarmReconfigurationCommandService
	 *            TaskFarmReconfigurationCommandService} waits this amount of adaptation thread iterations for performance improvements after a new worker stage is
	 *            added
	 */
	public void setMaxSamplesUntilRemove(final int maxSamplesUntilRemove) {
		this.maxSamplesUntilRemove = maxSamplesUntilRemove;
	}

	/**
	 *
	 * @return throughput boundary of this task farm
	 */
	public double getThroughputScoreBoundary() {
		return throughputScoreBoundary;
	}

	/**
	 *
	 * @param throughputScoreBoundary
	 *            throughput boundary of this task farm
	 */
	public void setThroughputScoreBoundary(final double throughputScoreBoundary) {
		this.throughputScoreBoundary = throughputScoreBoundary;
	}

	/**
	 *
	 * @return should the monitoring services be activated (does not affect the adaptation thread!)?
	 */
	public boolean isMonitoringEnabled() {
		return monitoringEnabled;
	}

	/**
	 *
	 * @param monitoringEnabled
	 *            should the monitoring services be activated (does not affect the adaptation thread!)?
	 */
	public void setMonitoringEnabled(final boolean monitoringEnabled) {
		this.monitoringEnabled = monitoringEnabled;
	}

	/**
	 *
	 * @return the waiting time between each iteration of the adaptation thread
	 */
	public int getAdaptationWaitingTimeMillis() {
		return adaptationWaitingTimeMillis;
	}

	/**
	 *
	 * @param adaptationWaitingTimeMillis
	 *            the waiting time between each iteration of the adaptation thread
	 */
	public void setAdaptationWaitingTimeMillis(final int adaptationWaitingTimeMillis) {
		this.adaptationWaitingTimeMillis = adaptationWaitingTimeMillis;
	}

	/**
	 *
	 * @return pipe capacity of all pipes inside the task farm
	 */
	public int getPipeCapacity() {
		return pipeCapacity;
	}

	/**
	 *
	 * @param pipeCapacity
	 *            pipe capacity of all pipes inside the task farm
	 */
	public void setPipeCapacity(final int pipeCapacity) {
		this.pipeCapacity = pipeCapacity;
	}

	/**
	 *
	 * @return the maximum number of worker stages the task farm may have
	 */
	public int getMaxNumberOfCores() {
		return maxNumberOfCores;
	}

	/**
	 *
	 * @param maxNumberOfCores
	 *            the maximum number of worker stages the task farm may have
	 */
	public void setMaxNumberOfCores(final int maxNumberOfCores) {
		this.maxNumberOfCores = maxNumberOfCores;
	}
}
