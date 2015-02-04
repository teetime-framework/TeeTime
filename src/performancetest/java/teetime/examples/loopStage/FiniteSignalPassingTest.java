package teetime.examples.loopStage;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import teetime.framework.Analysis;

public class FiniteSignalPassingTest {

	@Test(timeout = 5000)
	// may not run infinitely, so we set an arbitrary timeout that is high enough
	public void testStartSignalDoesNotEndUpInInfiniteLoop() throws Exception {
		boolean exceptionsOccured = false;
		LoopStageAnalysisConfiguration configuration = new LoopStageAnalysisConfiguration();
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		try {
			analysis.start();
		} catch (RuntimeException e) {
			exceptionsOccured = true;
		}
		assertFalse(exceptionsOccured);
	}
}
