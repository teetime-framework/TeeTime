package teetime.variant.methodcallWithPorts.examples.loopStage;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import teetime.util.Pair;
import teetime.variant.methodcallWithPorts.framework.core.Analysis;

public class FiniteSignalPassingTest {

	@Test(timeout = 5000)
	// may not run infinitely, so we set an arbitrary timeout that is high enough
	public void testStartSignalDoesNotEndUpInInfiniteLoop() throws Exception {
		LoopStageAnalysisConfiguration configuration = new LoopStageAnalysisConfiguration();
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		Collection<Pair<Thread, Throwable>> exceptions = analysis.start();

		assertEquals(0, exceptions.size());
	}
}
