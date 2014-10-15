package teetime.examples.experiment01;

import static org.junit.Assert.assertEquals;
import util.test.PerformanceResult;
import util.test.PerformanceTest;
import util.test.ProfiledPerformanceAssertion;

public class ChwHomePerformanceCheck extends ProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwHome";
	}

	@Override
	public void check() {
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");

		assertEquals(292, test01.quantiles.get(0.5), 1);
	}
}