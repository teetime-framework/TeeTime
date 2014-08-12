package teetime.variant.methodcall.examples;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import teetime.variant.methodcall.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import teetime.variant.methodcall.examples.experiment02.MethodCallThoughputTimestampAnalysis2Test;
import teetime.variant.methodcall.examples.experiment03.MethodCallThoughputTimestampAnalysis3Test;
import teetime.variant.methodcall.examples.experiment04.MethodCallThoughputTimestampAnalysis4Test;
import teetime.variant.methodcall.examples.experiment05.MethodCallThoughputTimestampAnalysis5Test;
import teetime.variant.methodcall.examples.experiment06.MethodCallThoughputTimestampAnalysis6Test;
import teetime.variant.methodcall.examples.experiment07.MethodCallThoughputTimestampAnalysis7Test;
import teetime.variant.methodcall.examples.experiment08.MethodCallThoughputTimestampAnalysis8Test;
import teetime.variant.methodcall.examples.experiment12.MethodCallThoughputTimestampAnalysis12Test;
import teetime.variant.methodcall.examples.experiment13.MethodCallThoughputTimestampAnalysis13Test;
import util.PerformanceResult;
import util.PerformanceTest;

@RunWith(Suite.class)
@SuiteClasses({
	MethodCallThoughputTimestampAnalysis1Test.class,
	MethodCallThoughputTimestampAnalysis2Test.class,
	MethodCallThoughputTimestampAnalysis3Test.class,
	MethodCallThoughputTimestampAnalysis4Test.class,
	MethodCallThoughputTimestampAnalysis5Test.class,
	MethodCallThoughputTimestampAnalysis6Test.class,
	MethodCallThoughputTimestampAnalysis7Test.class,
	MethodCallThoughputTimestampAnalysis8Test.class,
	MethodCallThoughputTimestampAnalysis12Test.class,
	MethodCallThoughputTimestampAnalysis13Test.class,
})
public class NieComparisonMethodcall {

	@AfterClass
	public static void doYourOneTimeTeardown() {
		Map<String, PerformanceResult> performanceResults = PerformanceTest.measurementRepository.performanceResults;
		for (Entry<String, PerformanceResult> entry : performanceResults.entrySet()) {
			System.out.println("---> " + entry.getKey() + "\n" + entry.getValue());
		}

		PerformanceResult test1 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test4 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment04.MethodCallThoughputTimestampAnalysis4Test)");
		PerformanceResult test7 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment07.MethodCallThoughputTimestampAnalysis7Test)");
		PerformanceResult test3 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment03.MethodCallThoughputTimestampAnalysis3Test)");
		PerformanceResult test8 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment08.MethodCallThoughputTimestampAnalysis8Test)");
		PerformanceResult test12 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment12.MethodCallThoughputTimestampAnalysis12Test)");
		PerformanceResult test13 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment13.MethodCallThoughputTimestampAnalysis13Test)");
		PerformanceResult test5 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment05.MethodCallThoughputTimestampAnalysis5Test)");
		PerformanceResult test2 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment02.MethodCallThoughputTimestampAnalysis2Test)");
		PerformanceResult test6 = performanceResults
				.get("testWithManyObjects(teetime.variant.methodcall.examples.experiment06.MethodCallThoughputTimestampAnalysis6Test)");

		assertEquals(1, (double) test4.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(2, (double) test7.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(4, (double) test3.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(4, (double) test8.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(8, (double) test12.quantiles.get(0.5) / test1.quantiles.get(0.5), 1.1);
		assertEquals(8, (double) test13.quantiles.get(0.5) / test1.quantiles.get(0.5), 1.1);
		assertEquals(10, (double) test5.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(17, (double) test2.quantiles.get(0.5) / test1.quantiles.get(0.5), 0.1);
		assertEquals(65, (double) test6.quantiles.get(0.5) / test1.quantiles.get(0.5), 1.1);
	}

}
