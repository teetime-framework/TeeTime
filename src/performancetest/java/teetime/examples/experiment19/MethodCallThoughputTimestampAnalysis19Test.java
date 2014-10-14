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
package teetime.examples.experiment19;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.util.ConstructorClosure;
import teetime.util.ListUtil;
import teetime.util.TimestampObject;
import util.PerformanceTest;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MethodCallThoughputTimestampAnalysis19Test extends PerformanceTest {

	// TODO use @Parameter for the number of threads

	@Test
	public void testWithManyObjectsAnd1Thread() {
		this.performAnalysis(1);
	}

	@Test
	public void testWithManyObjectsAnd2Threads() {
		this.performAnalysis(2);
	}

	@Test
	public void testWithManyObjectsAnd4Threads() {
		this.performAnalysis(4);
	}

	// @AfterClass
	// public static void afterClass() {
	// PerformanceResult test16a = PerformanceTest.measurementRepository.performanceResults
	// .get("testWithManyObjectsAnd1Thread(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
	// PerformanceResult test16b = PerformanceTest.measurementRepository.performanceResults
	// .get("testWithManyObjectsAnd2Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
	// PerformanceResult test16c = PerformanceTest.measurementRepository.performanceResults
	// .get("testWithManyObjectsAnd4Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
	// // check speedup
	// assertEquals(2, (double) test16a.overallDurationInNs / test16b.overallDurationInNs, 0.2);
	// assertEquals(2.5, (double) test16a.overallDurationInNs / test16c.overallDurationInNs, 0.2);
	// }

	private void performAnalysis(final int numThreads) {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");

		final MethodCallThroughputAnalysis19 analysis = new MethodCallThroughputAnalysis19();
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
		}

		this.timestampObjects = ListUtil.merge(analysis.getTimestampObjectsList());
	}

}
