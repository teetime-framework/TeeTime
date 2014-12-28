package teetime.examples.experiment09pipeimpls;

import teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.MeasurementRepository;
import util.test.PerformanceResult;
import util.test.PerformanceTest;

abstract class AbstractPerformanceCheck extends AbstractProfiledPerformanceAssertion {

	protected PerformanceResult test01;
	protected PerformanceResult test09CommittablePipes;
	protected PerformanceResult test09SingleElementPipes;
	protected PerformanceResult test09OrderedGrowableArrayPipes;

	@Override
	public void check() {
		String testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis1Test.class, "testWithManyObjects");
		test01 = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testCommittablePipes");
		test09CommittablePipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testSingleElementPipes");
		test09SingleElementPipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testOrderedGrowableArrayPipes");
		test09OrderedGrowableArrayPipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);
	}

}
