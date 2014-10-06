package teetime.examples.experiment01;

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
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");

		assertEquals(410, test01.quantiles.get(0.5), 1);
	}
}
