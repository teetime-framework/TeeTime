package teetime.examples.experiment16;

import static org.junit.Assert.assertEquals;
import util.PerformanceCheckProfile;
import util.PerformanceResult;
import util.PerformanceTest;

public class ChwHomePerformanceCheck implements PerformanceCheckProfile {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwHome";
	}

	@Override
	public void check() {
		PerformanceResult test16a = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16b = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16c = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		// check speedup
		double speedupB = (double) test16a.overallDurationInNs / test16b.overallDurationInNs;
		double speedupC = (double) test16a.overallDurationInNs / test16c.overallDurationInNs;

		System.out.println("speedupB: " + speedupB);
		System.out.println("speedupC: " + speedupC);

		assertEquals(2, speedupB, 0.3);
		assertEquals(3.6, speedupC, 0.3);
	}
}
