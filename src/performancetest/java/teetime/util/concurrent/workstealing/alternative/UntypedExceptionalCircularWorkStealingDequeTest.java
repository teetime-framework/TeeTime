/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.util.concurrent.workstealing.alternative;

import org.hamcrest.number.OrderingComparison;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import teetime.util.StopWatch;
import teetime.util.concurrent.workstealing.exception.DequeIsEmptyException;

public class UntypedExceptionalCircularWorkStealingDequeTest {

	private StopWatch stopWatch;

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
	}

	@Test
	public void measureManyEmptyPulls() {
		final UntypedExceptionalCircularWorkStealingDeque deque = new UntypedExceptionalCircularWorkStealingDeque();
		System.out.println(UntypedExceptionalCircularWorkStealingDeque.DEQUE_IS_EMPTY_EXCEPTION);
		System.out.println(UntypedExceptionalCircularWorkStealingDeque.OPERATION_ABORTED_EXCEPTION);

		int counter = 0;
		final int numIterations = UntypedCircularWorkStealingDequeTest.NUM_ITERATIONS;
		this.stopWatch.start();
		for (int i = 0; i < numIterations; i++) {
			try {
				deque.popBottom();
			} catch (final DequeIsEmptyException e) {
				// do not handle; we just want to compare the performance of throwing a preallocated exception vs. returning special values
				counter++;
			}
		}
		this.stopWatch.end();

		Assert.assertThat(this.stopWatch.getDurationInNs(), OrderingComparison.lessThan(UntypedCircularWorkStealingDequeTest.EXPECTED_DURATION_IN_NS));
		Assert.assertThat(counter, OrderingComparison.comparesEqualTo(UntypedCircularWorkStealingDequeTest.NUM_ITERATIONS));
	}
}
