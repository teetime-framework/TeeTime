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
package teetime.stage.taskfarm.adaptation.analysis.algorithm;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import teetime.stage.taskfarm.adaptation.analysis.AbstractThroughputAlgorithm;
import teetime.stage.taskfarm.adaptation.analysis.algorithm.WeightedAlgorithm.WeightMethod;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

/**
 * @author Christian Claus Wiechmann
 */
public class WeightedAlgorithmTest {

	private ThroughputHistory history;

	@Before
	public void prepareHistory() {
		this.history = new ThroughputHistory();

		this.history.add(1);
		this.history.add(10);
		this.history.add(1);
		this.history.add(1);
	}

	@Test
	public void exponentialTest() {
		final AbstractThroughputAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.EXPONENTIAL, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.52)));
	}

	@Test
	public void linearTest() {
		final AbstractThroughputAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LINEAR, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.59)));
	}

	@Test
	public void logarithmicTest() {
		final AbstractThroughputAlgorithm algorithm = new WeightedAlgorithm(WeightMethod.LOGARITHMIC, null);
		final double thoughputScore = algorithm.getTroughputAnalysis(this.history);
		assertThat(thoughputScore, is(greaterThan(0.63)));
	}

}
