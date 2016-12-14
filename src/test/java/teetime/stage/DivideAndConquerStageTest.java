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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.DivideAndConquerStage;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;
import teetime.testutil.ArrayCreator;
import teetime.testutil.AssertHelper;

/**
 * @author Robin Mohr
 */
public class DivideAndConquerStageTest {

	private DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;

	@Before
	public void initialize() {
		quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();
	}

	@Test
	public void shouldSortEmptyArray() {
		int[] noInputValues = {};
		QuicksortProblem problem = new QuicksortProblem(noInputValues);
		List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		test(this.quicksortStage).and()
				.send(problem).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();

		assertThat(solutions.get(0).getNumbers(), is(noInputValues));
		assertThat(solutions.size(), is(1));
	}

	@Test
	public void shouldSortSingleArray() {
		int[] numbers = { 5, 4, 3, 2, 1 };
		QuicksortProblem problem = new QuicksortProblem(numbers);
		List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		test(this.quicksortStage).and()
				.send(problem).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();

		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(solutions.get(0).getNumbers(), sortedNumbers);
		assertThat(solutions.size(), is(1));
	}

	@Test
	public void shouldSortMultipleArrays() {
		List<QuicksortProblem> problems = new ArrayList<QuicksortProblem>();
		int[] firstInts = { 3, 8, 4, 2, 1, 9, 5, 10, 7, 6 };
		int[] secondInts = { 2, 4, 3, 1, 5 };
		int[] thirdInts = { 4, 3, 3, 4, 2, 4, 1, 2, 3, 4 };
		int[] fourthInts = { 4 };
		problems.add(new QuicksortProblem(firstInts));
		problems.add(new QuicksortProblem(secondInts));
		problems.add(new QuicksortProblem(thirdInts));
		problems.add(new QuicksortProblem(fourthInts));

		List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		test(this.quicksortStage).and()
				.send(problems).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();

		AssertHelper.assertSorted(solutions.get(0).getNumbers());
		AssertHelper.assertSorted(solutions.get(1).getNumbers());
		AssertHelper.assertSorted(solutions.get(2).getNumbers());
		AssertHelper.assertSorted(solutions.get(3).getNumbers());
		assertThat(solutions.size(), is(4));
	}

	@Test
	public void shouldSortHugeArray() {
		int[] numbers = new ArrayCreator(0).createFilledArray(10000000);
		QuicksortProblem problem = new QuicksortProblem(numbers);
		List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		test(this.quicksortStage).and()
				.send(problem).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();

		AssertHelper.assertSorted(solutions.get(0).getNumbers());
		assertThat(solutions.size(), is(1));
	}

	@Test
	public void shouldSortMultipleHugeArrays() {
		ArrayCreator arrayCreator = new ArrayCreator(0);
		List<QuicksortProblem> problems = new ArrayList<QuicksortProblem>();

		problems.add(new QuicksortProblem(arrayCreator.createFilledArray(10000000)));
		problems.add(new QuicksortProblem(arrayCreator.createFilledArray(10000000)));
		problems.add(new QuicksortProblem(arrayCreator.createFilledArray(10000000)));

		List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		test(this.quicksortStage).and()
				.send(problems).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();

		AssertHelper.assertSorted(solutions.get(0).getNumbers());
		AssertHelper.assertSorted(solutions.get(1).getNumbers());
		AssertHelper.assertSorted(solutions.get(2).getNumbers());
		assertThat(solutions.size(), is(problems.size()));
	}

}
