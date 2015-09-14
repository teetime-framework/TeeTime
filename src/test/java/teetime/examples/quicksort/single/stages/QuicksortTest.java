package teetime.examples.quicksort.single.stages;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

public class QuicksortTest {

	@Test
	public void executeTest() {
		int[] numbers = new int[] { 3, 1, 4, 5, 2 };
		QuicksortProblem problemOne = new QuicksortProblem(0, numbers.length - 1, numbers);

		ArrayList<QuicksortProblem> inputs = new ArrayList<QuicksortProblem>();
		inputs.add(problemOne);

		ArrayList<QuicksortSolution> outputs = new ArrayList<QuicksortSolution>();

		final QuicksortConfiguration configuration = new QuicksortConfiguration(inputs, outputs);
		final Execution<QuicksortConfiguration> execution = new Execution<QuicksortConfiguration>(configuration);
		execution.executeBlocking();

		int[] sortedNumbers = new int[] { 1, 2, 3, 4, 5 };
		assertArrayEquals(outputs.get(0).getNumbers(), sortedNumbers);
	}

}
