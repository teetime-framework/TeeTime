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
package teetime.stage.taskfarm.adaptation.analysis.algorithm;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.adaptation.analysis.AbstractThroughputAlgorithm;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

/**
 * Represents the analysis of the throughput of a certain amount of items
 * while giving more weight to more recent items. The weighting can
 * be calculated either logarithmically, linearly or exponentially.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class WeightedAlgorithm extends AbstractThroughputAlgorithm {

	/**
	 * This enumeration contains values for logarithmic, exponential
	 * or linear weighting.
	 *
	 * @author Christian Claus Wiechmann
	 *
	 */
	public enum WeightMethod {
		LOGARITHMIC,
		LINEAR,
		EXPONENTIAL
	}

	private final WeightMethod weightMethod;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public WeightedAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
		this.weightMethod = configuration.getWeightedAlgorithmMethod();
	}

	public WeightedAlgorithm(final WeightMethod weightMethod, final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
		this.weightMethod = weightMethod;
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double weightedSum = 0;
		double totalWeights = 0;

		// more recent entry means more weight
		for (int i = this.window; i > 0; i--) {
			final double weight = this.getWeight(i - 1);
			totalWeights += weight;
			weightedSum += history.getThroughputOfEntry(i) * weight;
		}

		return weightedSum / totalWeights;
	}

	private double getWeight(final double distance) {
		final double tempWeight = this.window - distance;
		double finalWeight;

		switch (this.weightMethod) {
		case LOGARITHMIC:
			finalWeight = Math.log(tempWeight);
			break;
		case LINEAR:
			finalWeight = tempWeight;
			break;
		case EXPONENTIAL:
			finalWeight = Math.exp(tempWeight);
			break;
		default:
			finalWeight = tempWeight;
			break;
		}

		return finalWeight;
	}
}
