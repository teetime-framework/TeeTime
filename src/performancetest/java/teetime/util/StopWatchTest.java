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
package teetime.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import util.test.eval.StatisticsUtil;

public class StopWatchTest {

	private static final int NUM_ITERATIONS = 1000000;

	@Test
	public void testNanotime() throws Exception {
		StopWatch iterationStopWatch = new StopWatch();

		List<Long> durationsInNs = new ArrayList<Long>(NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			iterationStopWatch.start();
			fib(BigInteger.valueOf(10l));
			iterationStopWatch.end();
			durationsInNs.add(iterationStopWatch.getDurationInNs());
		}

		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(durationsInNs);
		StatisticsUtil.getQuantilesString(quintiles);
	}

	public static BigInteger fib(final BigInteger n) {
		if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0) {
			return n;
		} else {
			return fib(n.subtract(BigInteger.ONE)).add(fib(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE)));
		}
	}
}
