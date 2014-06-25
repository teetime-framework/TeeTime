package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;

import kieker.common.logging.LogFactory;

public abstract class PerformanceTest {

	protected static final int NUM_OBJECTS_TO_CREATE = 100000;
	protected static final int NUM_NOOP_FILTERS = 800;

	private static MeasurementRepository measurementRepository = new MeasurementRepository();

	private Description description;

	protected StopWatch stopWatch;
	protected List<TimestampObject> timestampObjects;

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
		System.setProperty(LogFactory.CUSTOM_LOGGER_JVM, "NONE");
		this.stopWatch = new StopWatch();
		this.timestampObjects = new ArrayList<TimestampObject>(NUM_OBJECTS_TO_CREATE);
	}

	@After
	public void after() {
		PerformanceResult performanceResult = StatisticsUtil.printStatistics(this.stopWatch.getDurationInNs(), this.timestampObjects);
		measurementRepository.performanceResults.put(this.description.getDisplayName(), performanceResult);
	}
}
