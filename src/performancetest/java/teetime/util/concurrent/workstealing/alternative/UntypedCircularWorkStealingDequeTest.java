/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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

public class UntypedCircularWorkStealingDequeTest {

	public static final int NUM_ITERATIONS = 100000000;
	public static final long EXPECTED_DURATION_IN_NS = 1100*1000*1000;

	private StopWatch stopWatch;

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
	}

	@Test
	public void measureManyEmptyPulls() {
		final UntypedCircularWorkStealingDeque deque = new UntypedCircularWorkStealingDeque();

		final int numIterations = NUM_ITERATIONS;
		this.stopWatch.start();
		for (int i = 0; i < numIterations; i++) {
			deque.popBottom();
		}
		this.stopWatch.end();

		Assert.assertThat(this.stopWatch.getDurationInNs(), OrderingComparison.lessThan(UntypedCircularWorkStealingDequeTest.EXPECTED_DURATION_IN_NS));
	}
}
