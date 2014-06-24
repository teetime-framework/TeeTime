package test;

import org.junit.Before;

import kieker.common.logging.LogFactory;

public abstract class PerformanceTest {

	protected static final int NUM_OBJECTS_TO_CREATE = 100000;
	protected static final int NUM_NOOP_FILTERS = 800;

	@Before
	public void before() {
		System.setProperty(LogFactory.CUSTOM_LOGGER_JVM, "NONE");
	}
}
