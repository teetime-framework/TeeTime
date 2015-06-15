package teetime.framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RunnableProducerStageTest {

	@Test
	public void testInit() {
		RunnableTestStage testStage = new RunnableTestStage();
		RunnableProducerStage runnable = new RunnableProducerStage(testStage);
		Thread thread = new Thread(runnable);
		thread.start();
		// Not running and not initialized
		assertFalse(testStage.executed && testStage.initialized);
		runnable.triggerInitializingSignal();
		// Not running, but initialized
		assertFalse(testStage.executed && !testStage.initialized);
		runnable.triggerStartingSignal();

		while (!testStage.shouldBeTerminated()) {
			Thread.yield();
		}
		assertTrue(testStage.executed);
	}
}
