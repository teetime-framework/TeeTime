package teetime.variant.methodcallWithPorts.examples;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import util.PerformanceCheckProfile;
import util.PerformanceCheckProfileRepository;

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
public class ComparisonMethodcallWithPorts {

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("logback.configurationFile", "src/test/resources/logback-test.groovy");
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new ChwWorkComparisonMethodcallWithPorts());
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new ChwHomeComparisonMethodcallWithPorts());
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new NieWorkComparisonMethodcallWithPorts());
	};

	@AfterClass
	public static void compareResults() {
		PerformanceCheckProfile pcp = PerformanceCheckProfileRepository.INSTANCE.get(ComparisonMethodcallWithPorts.class);
		pcp.check();
	}

}
