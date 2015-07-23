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
 * This class contains the configuration of a single Task Farm.
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

	public static final int INIT_SAMPLES_UNTIL_REMOVE = -1;

	private volatile int analysisWindow = 3;
	private volatile String throughputAlgorithm = "RegressionAlgorithm";
	private volatile WeightMethod weightedAlgorithmMethod = WeightMethod.EXPONENTIAL;
	private volatile int maxSamplesUntilRemove = 5;
	private volatile double throughputScoreBoundary = 0.2d;

	private volatile boolean stillParallelizable = true;

	TaskFarmConfiguration() {}

	public int getAnalysisWindow() {
		return this.analysisWindow;
	}

	public void setAnalysisWindow(final int analysisWindow) {
		this.analysisWindow = analysisWindow;
	}

	public String getThroughputAlgorithm() {
		return throughputAlgorithm;
	}

	public void setThroughputAlgorithm(final String throughputAlgorithm) {
		this.throughputAlgorithm = throughputAlgorithm;
	}

	public WeightMethod getWeightedAlgorithmMethod() {
		return weightedAlgorithmMethod;
	}

	public void setWeightedAlgorithmMethod(final WeightMethod weightedAlgorithmMethod) {
		this.weightedAlgorithmMethod = weightedAlgorithmMethod;
	}

	public int getMaxSamplesUntilRemove() {
		return maxSamplesUntilRemove;
	}

	public void setMaxSamplesUntilRemove(final int maxSamplesUntilRemove) {
		this.maxSamplesUntilRemove = maxSamplesUntilRemove;
	}

	public double getThroughputScoreBoundary() {
		return throughputScoreBoundary;
	}

	public void setThroughputScoreBoundary(final double throughputScoreBoundary) {
		this.throughputScoreBoundary = throughputScoreBoundary;
	}

	public boolean isStillParallelizable() {
		return stillParallelizable;
	}

	public void setStillParallelizable(final boolean stillParallelizable) {
		this.stillParallelizable = stillParallelizable;
	}
}
