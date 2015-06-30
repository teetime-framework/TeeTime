package teetime.examples.quicksort;

import org.junit.Test;

import teetime.framework.Execution;

public class QuicksortTest {

	public QuicksortTest() {}

	@Test
	public void executeTest() {
		final int upperBound = 92;
		final int size = 10;
		final String printStream = "STDOUT";

		final QuicksortConfiguration configuration = new QuicksortConfiguration(upperBound, size, printStream);
		final Execution<QuicksortConfiguration> execution = new Execution<QuicksortConfiguration>(configuration);
		execution.executeBlocking();

		// Assert.assertTrue(Files.equal(new File(inputFile), new File(outputFile)));
	}

}
