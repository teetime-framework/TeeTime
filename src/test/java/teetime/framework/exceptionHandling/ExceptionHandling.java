package teetime.framework.exceptionHandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.Analysis;

public class ExceptionHandling {

	private Analysis analysis;

	@Before
	public void newInstances() {
		analysis = new Analysis(new ExceptionTestConfiguration(), new TestListenerFactory());
	}

	@Test(timeout = 5000, expected = RuntimeException.class)
	public void exceptionPassingAndTermination() {
		analysis.execute();
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread to early
	}

	@Test
	public void terminatesAllStages() {
		// TODO: more than one stage and check, if all are terminated (at least 3, each of every terminationtype)
		assertTrue(true);
	}
}
