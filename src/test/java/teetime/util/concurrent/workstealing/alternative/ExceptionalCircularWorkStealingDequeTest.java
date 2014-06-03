package teetime.util.concurrent.workstealing.alternative;

import org.hamcrest.number.OrderingComparison;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.util.StopWatch;
import teetime.util.concurrent.workstealing.exception.DequeIsEmptyException;

@Ignore
public class ExceptionalCircularWorkStealingDequeTest {

	private StopWatch stopWatch;

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
	}

	@Test
	public void measureManyEmptyPulls() {
		final ExceptionalCircularWorkStealingDeque<String> deque = new ExceptionalCircularWorkStealingDeque<String>();

		final int numIterations = UntypedCircularWorkStealingDequeTest.NUM_ITERATIONS;
		this.stopWatch.start();
		for (int i = 0; i < numIterations; i++) {
			try {
				deque.popBottom();
			} catch (final DequeIsEmptyException e) {
				// do not handle; we just want to compare the performance of throwing a preallocated exception vs. returning special values
			}
		}
		this.stopWatch.end();

		Assert.assertThat(this.stopWatch.getDurationInNs(), OrderingComparison.lessThan(UntypedCircularWorkStealingDequeTest.EXPECTED_DURATION_IN_NS));
	}
}
