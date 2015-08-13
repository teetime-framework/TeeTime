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
public class ExtendedDivideAndConquerStageTest {
	DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage;
	List<QuicksortSolution> solutions;
	List<QuicksortProblem> problems;

	private QuicksortProblem problemOne;
	private QuicksortProblem problemTwo;
	QuicksortProblem problemThree;

	@Before
	public void initialize() {
		quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();

		int[] firstInts = { 3, 8, 4, 2, 1, 9, 5, 10, 7, 6 };
		int[] secondInts = { 2, 4, 3, 1, 5 };
		int[] thirdInts = { 4, 3, 3, 4, 2, 4, 1, 2, 3, 4 };

		problemOne = new QuicksortProblem(1, 0, firstInts.length - 1, firstInts);
		problemTwo = new QuicksortProblem(2, 0, secondInts.length - 1, secondInts);
		problemThree = new QuicksortProblem(3, 0, thirdInts.length - 1, thirdInts);

		problems = new ArrayList<QuicksortProblem>();
		problems.add(problemOne);
		problems.add(problemTwo);
		problems.add(problemThree);

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
		int[] firstSortedInts = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int[] secondSortedInts = { 1, 2, 3, 4, 5 };
		int[] thirdSortedInts = { 1, 2, 2, 3, 3, 3, 4, 4, 4, 4 };
		assertArrayEquals(solutions.get(0).getNumbers(), firstSortedInts);
		assertArrayEquals(solutions.get(1).getNumbers(), secondSortedInts);
		assertArrayEquals(solutions.get(2).getNumbers(), thirdSortedInts);

	}
}
