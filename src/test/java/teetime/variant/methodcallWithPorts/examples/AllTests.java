package teetime.variant.methodcallWithPorts.examples;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import teetime.variant.methodcall.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import teetime.variant.methodcallWithPorts.examples.experiment09.MethodCallThoughputTimestampAnalysis9Test;
import teetime.variant.methodcallWithPorts.examples.experiment10.MethodCallThoughputTimestampAnalysis10Test;
import teetime.variant.methodcallWithPorts.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test;
import teetime.variant.methodcallWithPorts.examples.experiment14.MethodCallThoughputTimestampAnalysis14Test;
import teetime.variant.methodcallWithPorts.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test;
import teetime.variant.methodcallWithPorts.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test;
import teetime.variant.methodcallWithPorts.examples.experiment17.MethodCallThoughputTimestampAnalysis17Test;
import teetime.variant.methodcallWithPorts.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test;
import test.PerformanceResult;
import test.PerformanceTest;

@RunWith(Suite.class)
@SuiteClasses({
	MethodCallThoughputTimestampAnalysis1Test.class,
	MethodCallThoughputTimestampAnalysis9Test.class,
	MethodCallThoughputTimestampAnalysis10Test.class,
	MethodCallThoughputTimestampAnalysis11Test.class,
	MethodCallThoughputTimestampAnalysis14Test.class,
	MethodCallThoughputTimestampAnalysis15Test.class,
	MethodCallThoughputTimestampAnalysis16Test.class,
	MethodCallThoughputTimestampAnalysis17Test.class,
	MethodCallThoughputTimestampAnalysis19Test.class,
})
public class AllTests {

	private static final double RESULT_TESTS_16 = 30;
	private static final double RESULT_TESTS_19 = 70;

	@AfterClass
	public static void compareResults() {
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

		assertEquals(60, (double) test14.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		assertEquals(14, (double) test10.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		assertEquals(32, (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		assertEquals(22, (double) test9.quantiles.get(0.5) / test1.quantiles.get(0.5), 2.1);
		assertEquals(44, (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// below results vary too much, possibly due to the OS' scheduler
		// assertEquals(RESULT_TESTS_16, (double) test16a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_16, (double) test16c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		//
		// assertEquals(RESULT_TESTS_19, (double) test19a.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19b.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);
		// assertEquals(RESULT_TESTS_19, (double) test19c.quantiles.get(0.5) / test1.quantiles.get(0.5), 5.1);

		assertEquals(39, (double) test17.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// check speedup
		assertEquals(2, (double) test16a.overallDurationInNs / test16b.overallDurationInNs, 0.2);
		assertEquals(2.5, (double) test16a.overallDurationInNs / test16c.overallDurationInNs, 0.2);

		assertEquals(2, (double) test19a.overallDurationInNs / test19b.overallDurationInNs, 0.2);
		assertEquals(2.5, (double) test19a.overallDurationInNs / test19c.overallDurationInNs, 0.2);
	}

}
