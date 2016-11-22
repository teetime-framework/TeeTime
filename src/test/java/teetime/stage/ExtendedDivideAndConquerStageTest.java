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

import static org.junit.Assert.assertTrue;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.DivideAndConquerStage;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

/**
 * @author Robin Mohr
 */
public class ExtendedDivideAndConquerStageTest {
	DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;
	List<QuicksortSolution> solutions;
	List<QuicksortProblem> problems;

	private QuicksortProblem problemOne;
	private QuicksortProblem problemTwo;
	private QuicksortProblem problemThree;
	private QuicksortProblem problemFour;
	private QuicksortProblem problemFive;

	@Before
	public void initialize() {
		quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();

		int[] firstInts = { 3, 8, 4, 2, 1, 9, 5, 10, 7, 6 };
		int[] secondInts = { 2, 4, 3, 1, 5 };
		int[] thirdInts = { 4, 3, 3, 4, 2, 4, 1, 2, 3, 4 };
		int[] fourthInts = { 4 };
		int[] fifthInts = {};

		problemOne = new QuicksortProblem(0, firstInts.length - 1, firstInts);
		problemTwo = new QuicksortProblem(0, secondInts.length - 1, secondInts);
		problemThree = new QuicksortProblem(0, thirdInts.length - 1, thirdInts);
		problemFour = new QuicksortProblem(0, fourthInts.length - 1, fourthInts);
		problemFive = new QuicksortProblem(0, fifthInts.length - 1, fifthInts);

		problems = new ArrayList<QuicksortProblem>();
		problems.add(problemOne);
		problems.add(problemTwo);
		problems.add(problemThree);
		problems.add(problemFour);
		problems.add(problemFive);

		solutions = new ArrayList<QuicksortSolution>();
	}

	@Test
	@Ignore
	// FIXME runs infinitely so far
	public void quicksortImplementationShouldSortArray() {
		test(this.quicksortStage).and()
				.send(problems).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();
	}

	@After
	public void evaluate() {
		assertTrue(isSorted(solutions.get(0).getNumbers()));
		assertTrue(isSorted(solutions.get(1).getNumbers()));
		assertTrue(isSorted(solutions.get(2).getNumbers()));
	}

	private boolean isSorted(final int... ints) {
		for (int i = 1; i < ints.length; i++) {
			if (ints[i - 1] > ints[i]) {
				return false;
			}
		}
		return true;
	}
}
