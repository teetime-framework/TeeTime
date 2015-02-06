package teetime.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.exceptionHandling.TestListener;

public class ExceptionHandling {

	TestListener listener;
	Analysis analysis;

	@Before
	public void newInstances() {
		listener = new TestListener();
		analysis = new Analysis(new ExceptionTestConfiguration(), listener);
	}

	@Test(timeout = 5000)
	public void exceptionPassingAndTermination() {
		boolean exceptionByExecute = false;

		try {
			analysis.execute();
		} catch (RuntimeException e) {
			exceptionByExecute = true;
		}
		assertTrue(exceptionByExecute); // thread was killed
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread to early
	}

	@Test
	public void terminatesAllStages() {
		// TODO: more than one stage and check, if all are terminated (at least 3, each of every terminationtype)
		assertTrue(true);
	}
}
