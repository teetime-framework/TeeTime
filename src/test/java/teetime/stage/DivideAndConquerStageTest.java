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
package teetime.stage;

import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.DivideAndConquerStage;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

/**
 * @author Robin Mohr
 */
public class DivideAndConquerStageTest {
	DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;
	List<QuicksortSolution> solutions;
	List<QuicksortProblem> problems;

	// @Before
	public void initialize() {
		quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();

		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(1, 0, numbers.length - 1, numbers);

		problems = new ArrayList<QuicksortProblem>();
		problems.add(problemOne);

		solutions = new ArrayList<QuicksortSolution>();
	}

	// @Test
	// FIXME runs infinitely so far
	public void quicksortImplementationShouldSortArray() {
		test(this.quicksortStage).and()
				.send(problems).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();
	}

	@Test
	public void tester() {
		for (int i = 0; i < 100; i++) {
			initialize();
			quicksortImplementationShouldSortArray();
			evaluate();
		}
	}

	// @After
	public void evaluate() {
		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(solutions.get(0).getNumbers(), sortedNumbers);
	}
}
