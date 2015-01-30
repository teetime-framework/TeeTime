package teetime.framework;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import teetime.framework.exceptionHandling.TestListener;

public class ExceptionHandling {

	@Test
	public void exceptionPassingAndTermination() {
		TestListener listener = new TestListener();
		Analysis analysis = new Analysis(new ExceptionTestConfiguration(), listener);
		analysis.init();
		analysis.start();
		assertEquals(TestListener.exceptionInvoked, 2);
	}
}
