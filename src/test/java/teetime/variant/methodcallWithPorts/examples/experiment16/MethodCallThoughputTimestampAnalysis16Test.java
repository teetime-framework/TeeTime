/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package teetime.variant.methodcallWithPorts.examples.experiment16;

import java.util.List;

import org.junit.Test;

import teetime.util.ConstructorClosure;
import teetime.util.ListUtil;
import teetime.util.StatisticsUtil;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import test.PerformanceTest;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallThoughputTimestampAnalysis16Test extends PerformanceTest {

	@Test
	public void testWithManyObjectsAnd1Thread() {
		long durationWith1Thread = this.performAnalysis(1, -1);
		this.performAnalysis(2, durationWith1Thread);
		this.performAnalysis(4, durationWith1Thread);
		// this.performAnalysis(8, stopWatch.getDurationInNs());
	}

	private long performAnalysis(final int numThreads, final long durationWith1Thread) {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");

		final MethodCallThroughputAnalysis16 analysis = new MethodCallThroughputAnalysis16();
		analysis.setNumWorkerThreads(numThreads);
		analysis.setNumNoopFilters(NUM_NOOP_FILTERS);
		analysis.setInput(NUM_OBJECTS_TO_CREATE, new ConstructorClosure<TimestampObject>() {
			@Override
			public TimestampObject create() {
				return new TimestampObject();
			}
		});
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		// TODO refactor test

		List<TimestampObject> timestampObjects = ListUtil.merge(analysis.getTimestampObjectsList());
		StatisticsUtil.printStatistics(this.stopWatch.getDurationInNs(), timestampObjects);

		if (durationWith1Thread != -1) {
			double speedup = (double) durationWith1Thread / this.stopWatch.getDurationInNs();
			System.out.println("Speedup (from 1 to " + numThreads + " threads): " + String.format("%.2f", speedup));
		}

		return this.stopWatch.getDurationInNs();
	}

	public static void main(final String[] args) {
		MethodCallThoughputTimestampAnalysis16Test test = new MethodCallThoughputTimestampAnalysis16Test();
		test.before();
		test.testWithManyObjectsAnd1Thread();
	}
}
