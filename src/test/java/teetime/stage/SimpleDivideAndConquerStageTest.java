package teetime.stage;

import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.SimpleDivideAndConquerStage;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

/**
 * @author Robin Mohr
 */
public class SimpleDivideAndConquerStageTest {
	SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;
	List<QuicksortSolution> solutions;
	List<QuicksortProblem> problems;

	@Before
	public void initialize() {
		quicksortStage = new SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution>();

		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(1, 0, numbers.length - 1, numbers);

		problems = new ArrayList<QuicksortProblem>();
		problems.add(problemOne);

		solutions = new ArrayList<QuicksortSolution>();
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
		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(solutions.get(0).getNumbers(), sortedNumbers);
	}
}
