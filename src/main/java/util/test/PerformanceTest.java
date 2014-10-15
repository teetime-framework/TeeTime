package util.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import teetime.util.StopWatch;
import teetime.util.TimestampObject;

public abstract class PerformanceTest {

	protected static final PerformanceCheckProfileRepository PERFORMANCE_CHECK_PROFILE_REPOSITORY = PerformanceCheckProfileRepository.INSTANCE;
	protected static final int NUM_OBJECTS_TO_CREATE = 1000000;
	protected static final int NUM_NOOP_FILTERS = 800;

	public static final MeasurementRepository measurementRepository = new MeasurementRepository();

	protected Description description;

	protected StopWatch stopWatch;
	protected List<TimestampObject> timestampObjects;

	static {
		System.setProperty("logback.configurationFile", "src/test/resources/logback-test.groovy");
	}

	@Rule
	public final TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			PerformanceTest.this.description = description;
			// System.out.println("getDisplayName(): " + description.getDisplayName());
		}
	};

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
		this.timestampObjects = new ArrayList<TimestampObject>(NUM_OBJECTS_TO_CREATE);
	}

	@After
	public void after() {
		String testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(description.getTestClass(), description.getMethodName());
		PerformanceResult performanceResult = StatisticsUtil.computeStatistics(this.stopWatch.getDurationInNs(), this.timestampObjects);
		measurementRepository.performanceResults.put(testMethodIdentifier, performanceResult);

		addToRepository(performanceResult);

		System.out.println("Duration: " + TimeUnit.NANOSECONDS.toMillis(performanceResult.overallDurationInNs) + " ms");
		System.out.println("avg duration: " + TimeUnit.NANOSECONDS.toMicros(performanceResult.avgDurInNs) + " µs");
		System.out.println(StatisticsUtil.getQuantilesString(performanceResult.quantiles));
		System.out.println("confidenceWidth: " + performanceResult.confidenceWidthInNs + " ns");
		System.out.println("[" + TimeUnit.NANOSECONDS.toMicros(performanceResult.avgDurInNs - performanceResult.confidenceWidthInNs) + " µs, "
				+ TimeUnit.NANOSECONDS.toMicros(performanceResult.avgDurInNs + performanceResult.confidenceWidthInNs) + " µs]");
	}

	@Deprecated
	private void addToRepository(final PerformanceResult performanceResult) {
		measurementRepository.performanceResults.put(this.description.getDisplayName(), performanceResult);
	}

}
