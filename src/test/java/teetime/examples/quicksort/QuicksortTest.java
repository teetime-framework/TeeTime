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
package teetime.examples.quicksort;

// NOPMD relevant for tests
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.Configuration;
import teetime.framework.ConfigurationBuilder;
import teetime.framework.DivideAndConquerStage;
import teetime.framework.Execution;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

public class QuicksortTest {

	@Test
	public void executeTestWithDefaultConfiguration() {
		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(0, numbers.length - 1, numbers);

		List<QuicksortProblem> inputs = new ArrayList<QuicksortProblem>();
		inputs.add(problemOne);

		List<QuicksortSolution> outputs = new ArrayList<QuicksortSolution>();

		final QuicksortConfiguration configuration = new QuicksortConfiguration(inputs, outputs);
		final Execution<QuicksortConfiguration> execution = new Execution<>(configuration);
		execution.executeBlocking();

		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(outputs.get(0).getNumbers(), sortedNumbers);
	}

	@Test
	public void executeTestWithBuilderBasedConfiguration() {
		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(0, numbers.length - 1, numbers);

		ArrayList<QuicksortProblem> inputs = new ArrayList<QuicksortProblem>();
		inputs.add(problemOne);

		ArrayList<QuicksortSolution> outputs = new ArrayList<QuicksortSolution>();

		final QuicksortConfigurationFromBuilder configuration = new QuicksortConfigurationFromBuilder(inputs, outputs);
		final Execution<QuicksortConfigurationFromBuilder> execution = new Execution<>(configuration);
		execution.executeBlocking();

		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(outputs.get(0).getNumbers(), sortedNumbers);
	}

	@Test
	public void executeTestWithConfigurationCreatedByBuilder() {
		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(0, numbers.length - 1, numbers);

		ArrayList<QuicksortProblem> inputs = new ArrayList<QuicksortProblem>();
		inputs.add(problemOne);

		ArrayList<QuicksortSolution> outputs = new ArrayList<QuicksortSolution>();

		// set up quicksort stage since it should be declared active
		DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>(
				2);
		quicksortStage.declareActive();

		final Configuration configuration = ConfigurationBuilder
				.from(new InitialElementProducer<QuicksortProblem>(inputs)).to(quicksortStage)
				.end(new CollectorSink<QuicksortSolution>(outputs));

		final Execution<Configuration> execution = new Execution<>(configuration);
		execution.executeBlocking();

		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(outputs.get(0).getNumbers(), sortedNumbers);
	}

}
