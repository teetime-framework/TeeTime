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
package teetime.examples.experiment16;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.framework.Analysis;
import teetime.util.ConstructorClosure;
import teetime.util.ListUtil;
import teetime.util.TimestampObject;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceCheckProfileRepository;
import util.test.PerformanceTest;

/**
 * @author Christian Wulf
 *
 * @since 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// @RunWith(Parameterized.class)
public class MethodCallThoughputTimestampAnalysis16Test extends PerformanceTest {

	// private final int numThreads;
	//
	// @Parameters
	// public static Iterable<Object[]> data() {
	// return Arrays.asList(new Object[][] {
	// { 1 }, { 2 }, { 4 }
	// });
	// }
	//
	// public MethodCallThoughputTimestampAnalysis16Test(final int numThreads) {
	// this.numThreads = numThreads;
	// }

	@BeforeClass
	public static void beforeClass() {
		PerformanceCheckProfileRepository.INSTANCE.register(MethodCallThoughputTimestampAnalysis16Test.class, new ChwWorkPerformanceCheck());
		PerformanceCheckProfileRepository.INSTANCE.register(MethodCallThoughputTimestampAnalysis16Test.class, new ChwHomePerformanceCheck());
	}

	@AfterClass
	public static void afterClass() {
		AbstractProfiledPerformanceAssertion pcp = PerformanceCheckProfileRepository.INSTANCE.get(MethodCallThoughputTimestampAnalysis16Test.class);
		pcp.check();
	}

	@Test
	public void testWithManyObjectsAnd1Thread() {
		performAnalysis(1);
	}

	@Test
	public void testWithManyObjectsAnd2Threads() {
		performAnalysis(2);
	}

	@Test
	public void testWithManyObjectsAnd4Threads() {
		performAnalysis(4);
	}

	private void performAnalysis(final int numThreads) {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");

		final AnalysisConfiguration16 configuration = new AnalysisConfiguration16(numThreads, NUM_NOOP_FILTERS);
		configuration.setInput(NUM_OBJECTS_TO_CREATE, new ConstructorClosure<TimestampObject>() {
			@Override
			public TimestampObject create() {
				return new TimestampObject();
			}
		});
		configuration.build();

		final Analysis analysis = new Analysis(configuration);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		this.timestampObjects = ListUtil.merge(configuration.getTimestampObjectsList());
	}

}
