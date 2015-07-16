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
package teetime.stage.taskfarm.adaptation.analysis;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

public class MeanAlgorithmTest {

	private static final double EPSILON = 0.00001;

	@Test
	public void constantThoughputTestAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(40.5);
		history.add(40.8);
		history.add(40.8);
		history.add(60);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(lessThan(-0.1)));
	}

	@Test
	public void constantThoughputTestNoAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4.5);
		history.add(4.8);
		history.add(4.8);
		history.add(4.7);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void risingThoughputTestNoAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(7);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(lessThan(-0.1)));
	}

	@Test
	public void risingThoughputTestAnomaly() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(5);
		history.add(6);
		history.add(3);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(closeTo(0.25d, EPSILON)));
	}

	@Test
	public void irregularThoughputTest() {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(4);
		history.add(10);
		history.add(8);
		history.add(1);

		final AbstractThroughputAnalysisAlgorithm algorithm = new MeanAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);

		assertThat(thoughputScore, is(greaterThan(0.7)));
	}
}
