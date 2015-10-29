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
 * Represents the analysis of the throughput of a certain amount of
 * items and predicts the next value based on the mean value.
 *
 * @author Christian Claus Wiechmann
 *
 */
public class MeanAlgorithm extends AbstractThroughputAlgorithm {

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	public MeanAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		super(configuration);
	}

	@Override
	protected double doAnalysis(final ThroughputHistory history) {
		double sum = 0;

		for (int i = 1; i <= this.window; i++) {
			final double current = history.getThroughputOfEntry(i);
			sum += current;
		}

		return sum / this.window;
	}
}
