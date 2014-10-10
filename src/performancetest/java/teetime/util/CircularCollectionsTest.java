package teetime.util;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

import teetime.util.concurrent.workstealing.CircularArray;
import teetime.util.concurrent.workstealing.CircularIntArray;
import teetime.util.concurrent.workstealing.CircularModIntArray;
import teetime.util.list.CircularList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CircularCollectionsTest {

	private static final int NUM_OBJECTS_TO_CREATE_IN_POW2 = 2;
	private static final int NUM_OBJECTS_TO_CREATE = (int) Math.pow(2, NUM_OBJECTS_TO_CREATE_IN_POW2);
	private static final int NUM_ACCESSES = 100000000;
	private static final int WARMUP_ITERATIONS = 3;

	private static final Map<String, Long> durations = new LinkedHashMap<String, Long>();

	private StopWatch stopWatch;
	protected Description description;

	@Before
	public void setup() {
		this.stopWatch = new StopWatch();
	}

	@Rule
	public final TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			CircularCollectionsTest.this.description = description;
		}
	};

	@After
	public void tearDown() {
		CircularCollectionsTest.durations.put(this.description.getDisplayName(), this.stopWatch.getDurationInNs());
	}

	@Test
	public void testCircularIntArray() throws Exception {
		CircularIntArray<Object> circularArray = new CircularIntArray<Object>(NUM_OBJECTS_TO_CREATE_IN_POW2);
		for (int i = 0; i < NUM_OBJECTS_TO_CREATE; i++) {
			circularArray.put(i, new Object());
		}

		int warmupIterations = WARMUP_ITERATIONS;
		while (warmupIterations-- > 0) {
			for (int i = 0; i < NUM_ACCESSES; i++) {
				circularArray.getNext().toString();
			}
		}

		this.stopWatch.start();
		for (int i = 0; i < NUM_ACCESSES; i++) {
			circularArray.getNext().toString();
		}
		this.stopWatch.end();
	}

	@Test
	public void testCircularModIntArray() throws Exception {
		CircularModIntArray<Object> circularArray = new CircularModIntArray<Object>(NUM_OBJECTS_TO_CREATE_IN_POW2);
		for (int i = 0; i < NUM_OBJECTS_TO_CREATE; i++) {
			circularArray.put(i, new Object());
		}

		int warmupIterations = WARMUP_ITERATIONS;
		while (warmupIterations-- > 0) {
			for (int i = 0; i < NUM_ACCESSES; i++) {
				circularArray.getNext().toString();
			}
		}

		this.stopWatch.start();
		for (int i = 0; i < NUM_ACCESSES; i++) {
			circularArray.getNext().toString();
		}
		this.stopWatch.end();
	}

	@Test
	public void testCircularList() throws Exception {
		CircularList<Object> circularList = new CircularList<Object>();
		for (int i = 0; i < NUM_OBJECTS_TO_CREATE; i++) {
			circularList.add(new Object());
		}

		int warmupIterations = WARMUP_ITERATIONS;
		while (warmupIterations-- > 0) {
			for (int i = 0; i < NUM_ACCESSES; i++) {
				circularList.getNext().toString();
			}
		}

		this.stopWatch.start();
		for (int i = 0; i < NUM_ACCESSES; i++) {
			circularList.getNext().toString();
		}
		this.stopWatch.end();
	}

	@Test
	public void testCircularLongArray() throws Exception {
		CircularArray<Object> circularArray = new CircularArray<Object>(NUM_OBJECTS_TO_CREATE_IN_POW2);
		for (int i = 0; i < NUM_OBJECTS_TO_CREATE; i++) {
			circularArray.put(i, new Object());
		}

		int warmupIterations = WARMUP_ITERATIONS;
		while (warmupIterations-- > 0) {
			for (int i = 0; i < NUM_ACCESSES; i++) {
				circularArray.getNext().toString();
			}
		}

		this.stopWatch.start();
		for (int i = 0; i < NUM_ACCESSES; i++) {
			circularArray.getNext().toString();
		}
		this.stopWatch.end();
	}

	@AfterClass
	public static void afterClass() {
		Long circularIntArrayInNs = durations.get("testCircularIntArray(teetime.util.CircularCollectionsTest)");
		Long circularModIntArrayInNs = durations.get("testCircularModIntArray(teetime.util.CircularCollectionsTest)");
		Long circularListInNs = durations.get("testCircularList(teetime.util.CircularCollectionsTest)");
		Long circularLongArrayInNs = durations.get("testCircularLongArray(teetime.util.CircularCollectionsTest)");

		for (Entry<String, Long> entry : durations.entrySet()) {
			System.out.println(entry.getKey() + ": " + TimeUnit.NANOSECONDS.toMillis(entry.getValue()) + " ms");
		}

		assertThat(circularListInNs, is(lessThan(circularModIntArrayInNs)));

		// testCircularIntArray(teetime.util.CircularCollectionsTest): 13202 ms
		// testCircularList(teetime.util.CircularCollectionsTest): 13957 ms
		// testCircularLongArray(teetime.util.CircularCollectionsTest): 12620 ms
		// testCircularModIntArray(teetime.util.CircularCollectionsTest): 14015 ms
	}
}
