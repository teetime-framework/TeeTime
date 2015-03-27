package teetime.framework.exceptionHandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.Analysis;

public class ExceptionHandling {

	private Analysis analysis;

	// @Before
	public void newInstances() {
		analysis = new Analysis(new ExceptionTestConfiguration(), new TestListenerFactory());
	}

	// @Test(timeout = 5000, expected = RuntimeException.class)
	public void exceptionPassingAndTermination() {
		analysis.execute();
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread to early
	}

	@Test
	public void terminatesAllStages() {
		// TODO: more than one stage and check, if all are terminated (at least 3, each of every terminationtype)
		assertTrue(true);
	}

	/**
	 * If the consumer is terminated first while the pipe is full, the finite producer will be locked in
	 * SpScPipe.add and cycle through the sleep method. As a result, the thread will never return to the point
	 * where it checks if it should be terminated.
	 */
	@Test(timeout = 30000)
	public void forAFewTimes() {
		for (int i = 0; i < 1000; i++) {
			newInstances();
			try {
				exceptionPassingAndTermination();
			} catch (RuntimeException e) {
				// TODO: handle exception
			}
			System.out.println(i);
		}
	}
}
