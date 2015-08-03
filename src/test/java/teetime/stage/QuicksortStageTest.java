package teetime.stage;

import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;
import teetime.stage.quicksort.QuicksortStage;

/**
 * @author Robin Mohr
 */
public class QuicksortStageTest {

	@Test
	public void quicksortImplementationShouldSortArray() {
		final QuicksortStage quicksortStage = new QuicksortStage();

		final int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		final int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };

		final QuicksortProblem problem = new QuicksortProblem(1, 0, numbers.length - 1, numbers);

		final List<QuicksortProblem> problems = new ArrayList<QuicksortProblem>();
		final List<QuicksortSolution> solutions = new ArrayList<QuicksortSolution>();

		problems.add(problem);

		test(quicksortStage).and().send(problems).to(quicksortStage.getInputPort()).and().receive(solutions).from(quicksortStage.getOutputPort()).start();

		System.out.println(solutions.get(1).toString());
		assertArrayEquals(solutions.get(1).getNumbers(), sortedNumbers);
	}
}
