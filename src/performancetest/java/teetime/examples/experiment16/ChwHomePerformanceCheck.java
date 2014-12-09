package teetime.examples.experiment16;

import static org.junit.Assert.assertEquals;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceResult;
import util.test.PerformanceTest;

class ChwHomePerformanceCheck extends AbstractProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwHome";
	}

	@Override
	public void check() {
		PerformanceResult test16a = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd1Thread(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		PerformanceResult test16b = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd2Threads(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		PerformanceResult test16c = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd4Threads(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		// check speedup
		double speedupB = (double) test16a.overallDurationInNs / test16b.overallDurationInNs;
		double speedupC = (double) test16a.overallDurationInNs / test16c.overallDurationInNs;

		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupB: " + speedupB);
		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupC: " + speedupC);

		// assertEquals(2, speedupB, 0.3);
		// since 31.08.2014 (incl.)
		// assertEquals(3.6, speedupC, 0.3);
		// since 04.11.2014 (incl.)
		// assertEquals(5, speedupC, 0.4);
		// since 07.12.2014 (incl.)
		assertEquals(2, speedupB, 0.4);
		assertEquals(5, speedupC, 0.4);
	}
}
