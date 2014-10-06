package teetime.examples;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import util.PerformanceCheckProfile;
import util.PerformanceResult;
import util.PerformanceTest;

public class NieWorkComparisonMethodcallWithPorts implements PerformanceCheckProfile {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "NieWork";
	}

	@Override
	public void check() {
		Map<String, PerformanceResult> performanceResults = PerformanceTest.measurementRepository.performanceResults;
		for (Entry<String, PerformanceResult> entry : performanceResults.entrySet()) {
			System.out.println("---> " + entry.getKey() + "\n" + entry.getValue());
		}

		PerformanceResult test1 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test9 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment09.MethodCallThoughputTimestampAnalysis9Test)");
		PerformanceResult test10 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment10.MethodCallThoughputTimestampAnalysis10Test)");
		PerformanceResult test11 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test)");
		PerformanceResult test14 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment14.MethodCallThoughputTimestampAnalysis14Test)");
		PerformanceResult test15 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test)");
		PerformanceResult test16a = performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.variant.methodcallWithPorts.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16b = performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.variant.methodcallWithPorts.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16c = performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.variant.methodcallWithPorts.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test17 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcallWithPorts.examples.experiment17.MethodCallThoughputTimestampAnalysis17Test)");
		PerformanceResult test19a = performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.variant.methodcallWithPorts.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19b = performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.variant.methodcallWithPorts.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19c = performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.variant.methodcallWithPorts.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");

		assertEquals(67, (double) test14.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		assertEquals(14, (double) test10.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		assertEquals(39, (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		assertEquals(35, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		assertEquals(58, (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// below results vary too much, possibly due to the OS' scheduler
		// assertEquals(RESULT_TESTS_16, (double) test16a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		//
		// assertEquals(RESULT_TESTS_19, (double) test19a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);

		assertEquals(56, (double) test17.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// check speedup
		assertEquals(2, (double) test16a.overallDurationInNs / test16b.overallDurationInNs, 0.2);
		assertEquals(3.7, (double) test16a.overallDurationInNs / test16c.overallDurationInNs, 0.2);

		assertEquals(2, (double) test19a.overallDurationInNs / test19b.overallDurationInNs, 0.2);
		assertEquals(3.7, (double) test19a.overallDurationInNs / test19c.overallDurationInNs, 0.2);
	}

}
