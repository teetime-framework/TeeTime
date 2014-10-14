package teetime.examples.experiment10;

import static org.junit.Assert.assertEquals;
import util.PerformanceCheckProfile;
import util.PerformanceResult;
import util.PerformanceTest;

public class ChwWorkPerformanceCheck implements PerformanceCheckProfile {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}

	@Override
	public void check() {
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test10 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment10.MethodCallThoughputTimestampAnalysis10Test)");

		double medianSpeedup = (double) test10.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (10): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(14, (double) test10.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// since 26.06.2014 (incl.)
		// assertEquals(26, meanSpeedup, 2.1); // +14
		// since 04.07.2014 (incl.)
		// assertEquals(26, meanSpeedup, 2.1); // +0
		// since 27.08.2014 (incl.)
		// assertEquals(56, meanSpeedup, 2.1); // +30
		// since 14.10.2014 (incl.)
		assertEquals(56, medianSpeedup, 2.1); // +0
	}
}
