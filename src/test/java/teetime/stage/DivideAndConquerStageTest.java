package teetime.stage;

import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.DivideAndConquerStage;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

/**
 * @author Robin Mohr
 */
public class DivideAndConquerStageTest {
	DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;
	int[] numbers;
	int[] sortedNumbers;
	QuicksortProblem problemOne;
	QuicksortProblem problemTwo;
	List<QuicksortSolution> solutions;
	List<QuicksortProblem> problems;

	@Before
	public void initialize() {
		quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();

		numbers = new int[] { 3, 1, 4, 5, 2 };
		sortedNumbers = new int[] { 1, 2, 3, 4, 5 };

		problemOne = new QuicksortProblem(1, 0, numbers.length - 1, numbers);

		problems = new ArrayList<QuicksortProblem>();
		solutions = new ArrayList<QuicksortSolution>();

		problems.add(problemOne);
	}

	@Test
	public void quicksortImplementationShouldSortArray() {

		test(this.quicksortStage).and()
				.send(problems).to(quicksortStage.getInputPort()).and()
				.receive(solutions).from(quicksortStage.getOutputPort())
				.start();
	}

	@After
	public void evaluate() {
		System.out.println(solutions.get(0).toString());
		assertArrayEquals(solutions.get(0).getNumbers(), sortedNumbers);
	}
}
