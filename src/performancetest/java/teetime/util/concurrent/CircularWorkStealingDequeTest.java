package teetime.util.concurrent;

import org.hamcrest.number.OrderingComparison;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import teetime.util.StopWatch;
import teetime.util.concurrent.workstealing.CircularWorkStealingDeque;
import teetime.util.concurrent.workstealing.alternative.UntypedCircularWorkStealingDequeTest;

public class CircularWorkStealingDequeTest {
	private StopWatch stopWatch;

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
	}

	@Test
	public void measureManyEmptyPulls() {
		final CircularWorkStealingDeque<Object> deque = new CircularWorkStealingDeque<Object>();

		final int numIterations = UntypedCircularWorkStealingDequeTest.NUM_ITERATIONS;
		this.stopWatch.start();
		for (int i = 0; i < numIterations; i++) {
			deque.popBottom();
		}
		this.stopWatch.end();

		Assert.assertThat(this.stopWatch.getDurationInNs(),
				OrderingComparison.lessThan(UntypedCircularWorkStealingDequeTest.EXPECTED_DURATION_IN_NS));
	}
}
