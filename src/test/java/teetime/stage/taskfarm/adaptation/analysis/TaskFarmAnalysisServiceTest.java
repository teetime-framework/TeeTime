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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmConfiguration;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.MeanAlgorithm;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.RegressionAlgorithm;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

public class TaskFarmAnalysisServiceTest {

	@SuppressWarnings("rawtypes")
	private final TaskFarmConfiguration configuration = createConfiguration();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final TaskFarmAnalysisService analyzer = new TaskFarmAnalysisService(configuration);
	private final ThroughputHistory history = new ThroughputHistory();
	private double throughputScore = 0;

	@Test
	public void analyzerTest() {
		checkAnalyzerWithAlgorithm("MeanAlgorithm", MeanAlgorithm.class);
		checkAnalyzerWithAlgorithm("WeightedAlgorithm", WeightedAlgorithm.class);
		checkAnalyzerWithAlgorithm("RegressionAlgorithm", RegressionAlgorithm.class);
	}

	@SuppressWarnings("rawtypes")
	private void checkAnalyzerWithAlgorithm(final String algorithmName, final Class algorithmClass) {
		configuration.setThroughputAlgorithm(algorithmName);
		analyzer.analyze(history);
		throughputScore = analyzer.getThroughputScore();
		assertThat(throughputScore, is(equalTo((double) AbstractThroughputAlgorithm.INVALID_SCORE)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TaskFarmConfiguration createConfiguration() {
		TaskFarmStage taskFarmStage = new TaskFarmStage(new DummyDuplicableStage());
		TaskFarmConfiguration configuration = taskFarmStage.getConfiguration();
		return configuration;
	}

	@SuppressWarnings("rawtypes")
	private class DummyDuplicableStage extends AbstractFilter implements ITaskFarmDuplicable {
		@Override
		public ITaskFarmDuplicable duplicate() {
			return null;
		}

		@Override
		protected void execute(final Object element) {
			// nothing to do
		}
	}
}
