/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.adaptation.analysis;

import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

/**
 * Represents the calculation of the throughput score
 * by means of the prediction value calculated by the
 * concrete Throughput Algorithm.
 *
 * @author Christian Claus Wiechmann
 *
 */
public abstract class AbstractThroughputAlgorithm {

	/**
	 * Number of recently measured throughput values that we will analyze.
	 */
	protected final int window;

	/** represents an invalid throughput score **/
	public static final int INVALID_SCORE = -2;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this algorithm is used for
	 */
	protected AbstractThroughputAlgorithm(final TaskFarmConfiguration<?, ?, ?> configuration) {
		if (configuration == null) {
			// for testing purposes
			this.window = 3;
		} else {
			this.window = configuration.getAnalysisWindow();
		}
	}

	/**
	 * Calculates a prediction for the most recent ThroughputEntry by means
	 * of the last "window" entries.
	 *
	 * @param history
	 *            measured throughput values + timestamps
	 * @return throughput prediction for the most recent value
	 */
	protected abstract double doAnalysis(ThroughputHistory history);

	/**
	 * Calculates the throughput score for a given history.
	 *
	 * @param history
	 *            history of measured values
	 * @return throughput score
	 */
	public final double getTroughputAnalysis(final ThroughputHistory history) {
		if (!this.isHistoryLargeEnough(history)) {
			return INVALID_SCORE;
		}

		final double predicted = this.doAnalysis(history);
		final double lastValue = history.getThroughputOfEntry(0);

		final double difference = predicted - lastValue;
		final double sum = predicted + lastValue;

		if (sum == 0) {
			return 0;
		} else {
			return difference / sum;
		}
	}

	private boolean isHistoryLargeEnough(final ThroughputHistory history) {
		return history.getEntries().size() >= this.window + 1;
	}
}
