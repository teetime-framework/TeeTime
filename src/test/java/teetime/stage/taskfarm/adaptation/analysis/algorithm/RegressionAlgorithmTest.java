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
package teetime.stage.taskfarm.adaptation.analysis.algorithm;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import teetime.stage.taskfarm.adaptation.analysis.AbstractThroughputAlgorithm;
import teetime.stage.taskfarm.adaptation.history.ThroughputHistory;

public class RegressionAlgorithmTest {

	private static final double EPSILON = 0.1d;

	@Test
	public void positiveRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(1);
		Thread.sleep(100);
		history.add(2);
		Thread.sleep(100);
		history.add(3);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(5);

		final AbstractThroughputAlgorithm algorithm = new RegressionAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void negativeRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(8);
		Thread.sleep(100);
		history.add(6);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(2);

		final AbstractThroughputAlgorithm algorithm = new RegressionAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void boundedRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(7);
		Thread.sleep(100);
		history.add(4);
		Thread.sleep(100);
		history.add(1);
		Thread.sleep(100);
		history.add(0);

		final AbstractThroughputAlgorithm algorithm = new RegressionAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(closeTo(0, EPSILON)));
	}

	@Test
	public void falseRegression() throws InterruptedException {
		final ThroughputHistory history = new ThroughputHistory();

		history.add(10);
		Thread.sleep(100);
		history.add(9);
		Thread.sleep(100);
		history.add(8);
		Thread.sleep(100);
		history.add(7);
		Thread.sleep(100);
		history.add(13);

		final AbstractThroughputAlgorithm algorithm = new RegressionAlgorithm(null);
		final double thoughputScore = algorithm.getTroughputAnalysis(history);
		assertThat(thoughputScore, is(lessThan(-0.3d)));
	}

}
