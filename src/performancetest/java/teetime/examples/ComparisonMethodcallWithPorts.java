package teetime.examples;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import teetime.examples.experiment09.MethodCallThoughputTimestampAnalysis9Test;
import teetime.examples.experiment10.MethodCallThoughputTimestampAnalysis10Test;
import teetime.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test;
import teetime.examples.experiment14.MethodCallThoughputTimestampAnalysis14Test;
import teetime.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test;
import teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test;
import teetime.examples.experiment17.MethodCallThoughputTimestampAnalysis17Test;
import teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test;
import util.test.PerformanceCheckProfileRepository;
import util.test.ProfiledPerformanceAssertion;

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
		ProfiledPerformanceAssertion pcp = PerformanceCheckProfileRepository.INSTANCE.get(ComparisonMethodcallWithPorts.class);
		pcp.check();
	}

}
