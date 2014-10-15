package teetime.examples.experiment11;

import static org.junit.Assert.assertEquals;
import util.test.PerformanceResult;
import util.test.PerformanceTest;
import util.test.ProfiledPerformanceAssertion;

public class ChwWorkPerformanceCheck extends ProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}

	@Override
	public void check() {
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test11 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test)");

		double medianSpeedup = (double) test11.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (11): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(32, (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		// since 26.06.2014 (incl.)
		// assertEquals(44, medianSpeedup, 4.1); // +12
		// since 04.07.2014 (incl.)
		// assertEquals(41, medianSpeedup, 4.1); // -3
		// since 27.08.2014 (incl.)
		// assertEquals(64, medianSpeedup, 4.1); // +15
		// since 14.10.2014 (incl.)
		assertEquals(44, medianSpeedup, 4.1); // -20
	}
}
