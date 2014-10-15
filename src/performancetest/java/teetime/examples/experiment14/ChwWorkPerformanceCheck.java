package teetime.examples.experiment14;

import static org.junit.Assert.assertEquals;
import teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import util.test.MeasurementRepository;
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
		String testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis1Test.class, "testWithManyObjects");
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);
		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis14Test.class, "testWithManyObjects");
		PerformanceResult test14 = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

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
