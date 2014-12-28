package teetime.examples.experiment01;

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
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");

		assertEquals(292, test01.quantiles.get(0.5), 1);
	}
}
