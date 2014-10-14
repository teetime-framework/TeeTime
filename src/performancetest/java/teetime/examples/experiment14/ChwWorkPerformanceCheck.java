package teetime.examples.experiment14;

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
		PerformanceResult test14 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment14.MethodCallThoughputTimestampAnalysis14Test)");

		double medianSpeedup = (double) test14.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (14): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(60, (double) test14.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// since 26.06.2014 (incl.)
		// assertEquals(76, medianSpeedup, 5.1); // +16
		// since 04.07.2014 (incl.)
		// assertEquals(86, medianSpeedup, 5.1); // +16
		// since 27.08.2014 (incl.)
		// assertEquals(102, medianSpeedup, 5.1); // +16
		// since 14.10.2014 (incl.)
		assertEquals(53, medianSpeedup, 5.1); // -49
	}
}
