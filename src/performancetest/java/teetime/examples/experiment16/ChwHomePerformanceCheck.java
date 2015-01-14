package teetime.examples.experiment16;

import static org.junit.Assert.assertEquals;
import teetime.examples.HostName;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceResult;
import util.test.PerformanceTest;

class ChwHomePerformanceCheck extends AbstractProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return HostName.CHW_HOME.toString();
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
		double speedupA2B = (double) test16a.overallDurationInNs / test16b.overallDurationInNs;
		double speedupB2C = (double) test16b.overallDurationInNs / test16c.overallDurationInNs;

		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupB: " + speedupA2B);
		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupC: " + speedupB2C);

		// assertEquals(2, speedupB, 0.3);
		// since 31.08.2014 (incl.)
		// assertEquals(3.6, speedupC, 0.3);
		// since 04.11.2014 (incl.)
		// assertEquals(5, speedupC, 0.4);
		// since 07.12.2014 (incl.)
		// assertEquals(2, speedupA2B, 0.4);
		// assertEquals(5, speedupB2C, 0.4);
		// since 28.12.2014 (incl.)
		assertEquals(2, speedupA2B, 0.4);
		assertEquals(2, speedupB2C, 0.4);
	}
}
