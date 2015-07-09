package teetime.examples.quicksort;

import java.util.Random;

import org.junit.Test;

import teetime.framework.Execution;

public class QuicksortTest {

	public QuicksortTest() {}

	@Test
	public void executeTest() {
		final int[] arr = generateRandomNumbers(100);

		final QuicksortConfiguration configuration = new QuicksortConfiguration(arr);
		final Execution<QuicksortConfiguration> execution = new Execution<QuicksortConfiguration>(configuration);
		execution.executeBlocking();

		// Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

	private int[] generateRandomNumbers(final int n) {

		int[] arr = new int[n];
		Random random = new Random();

		for (int i = 0; i < n; i++) {
			arr[i] = (random.nextInt(n * 10));
		}

		return arr;
	}

}
