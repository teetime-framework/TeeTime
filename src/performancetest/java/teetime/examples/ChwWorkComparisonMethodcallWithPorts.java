package teetime.examples;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import util.test.PerformanceResult;
import util.test.PerformanceTest;
import util.test.ProfiledPerformanceAssertion;

public class ChwWorkComparisonMethodcallWithPorts extends ProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}

	@Override
	public void check() {
		Map<String, PerformanceResult> performanceResults = PerformanceTest.measurementRepository.performanceResults;
		for (Entry<String, PerformanceResult> entry : performanceResults.entrySet()) {
			System.out.println("---> " + entry.getKey() + "\n" + entry.getValue());
		}

		PerformanceResult test1 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test9 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment09.MethodCallThoughputTimestampAnalysis9Test)");
		PerformanceResult test11 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test)");
		PerformanceResult test15 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test)");
		PerformanceResult test16a = performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16b = performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16c = performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test17 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment17.MethodCallThoughputTimestampAnalysis17Test)");
		PerformanceResult test19a = performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19b = performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19c = performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");

		double value11 = (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5);
		double value9 = (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5);
		double value15 = (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5);
		double value17 = (double) test17.quantiles.get(0.5) / test1.quantiles.get(0.5);

		System.out.println("value11: " + value11);
		System.out.println("value9: " + value9);
		System.out.println("value15: " + value15);
		System.out.println("value17: " + value17);

		// until 25.06.2014 (incl.)
		// assertEquals(32, (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		// assertEquals(22, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		// assertEquals(44, (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		// assertEquals(39, (double) test17.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// since 26.06.2014 (incl.)
		// assertEquals(44, value11, 4.1); // +12
		// assertEquals(36, value9, 2.1); // +14
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +14

		// since 04.07.2014 (incl.)
		// assertEquals(41, value11, 4.1); // -3
		// assertEquals(42, value9, 2.1); // +6
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +0

		// since 27.08.2014 (incl.)
		// assertEquals(64, value11, 4.1); // +15
		// assertEquals(77, value9, 2.1); // +35
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +0

		// since 14.10.2014 (incl.)
		assertEquals(44, value11, 4.1); // -20
		assertEquals(77, value9, 2.1); // +35
		assertEquals(44, value15, 4.1); // +0
		assertEquals(53, value17, 4.1); // +0

		// below results vary too much, possibly due to the OS' scheduler
		// assertEquals(RESULT_TESTS_16, (double) test16a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		//
		// assertEquals(RESULT_TESTS_19, (double) test19a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);

		// check speedup
		assertEquals(2, (double) test16a.overallDurationInNs / test16b.overallDurationInNs, 0.3);
		assertEquals(2.5, (double) test16a.overallDurationInNs / test16c.overallDurationInNs, 0.2);

		assertEquals(2, (double) test19a.overallDurationInNs / test19b.overallDurationInNs, 0.3);
		assertEquals(2.5, (double) test19a.overallDurationInNs / test19c.overallDurationInNs, 0.3);
	}

}
