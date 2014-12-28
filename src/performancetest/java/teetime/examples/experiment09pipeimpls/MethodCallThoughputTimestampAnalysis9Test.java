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
package teetime.examples.experiment09pipeimpls;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teetime.framework.pipe.CommittablePipeFactory;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceTest;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThoughputTimestampAnalysis9Test extends PerformanceTest {

	@BeforeClass
	public static void beforeClass() {
		PERFORMANCE_CHECK_PROFILE_REPOSITORY.register(MethodCallThoughputTimestampAnalysis9Test.class, new ChwWorkPerformanceCheck());
		PERFORMANCE_CHECK_PROFILE_REPOSITORY.register(MethodCallThoughputTimestampAnalysis9Test.class, new ChwHomePerformanceCheck());
	}

	@AfterClass
	public static void afterClass() {
		AbstractProfiledPerformanceAssertion performanceCheckProfile = PERFORMANCE_CHECK_PROFILE_REPOSITORY.get(MethodCallThoughputTimestampAnalysis9Test.class);
		performanceCheckProfile.check();
	}

	@Test
	public void testCommittablePipes() throws Exception {
		IPipeFactory pipeFactory = new CommittablePipeFactory();
		testWithManyObjects(pipeFactory);
	}

	@Test
	public void testSingleElementPipes() throws Exception {
		IPipeFactory pipeFactory = new SingleElementPipeFactory();
		testWithManyObjects(pipeFactory);
	}

	private void testWithManyObjects(final IPipeFactory pipeFactory) {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");

		final MethodCallThroughputAnalysis9 analysis = new MethodCallThroughputAnalysis9();
		analysis.setNumNoopFilters(NUM_NOOP_FILTERS);
		analysis.setTimestampObjects(this.timestampObjects);
		analysis.setInput(NUM_OBJECTS_TO_CREATE, new ConstructorClosure<TimestampObject>() {
			@Override
			public TimestampObject create() {
				return new TimestampObject();
			}
		});
		analysis.init(pipeFactory);

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}
	}

}
