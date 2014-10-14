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
package teetime.examples.experiment17;

import org.junit.Test;

import teetime.util.ConstructorClosure;
import teetime.util.ListUtil;
import teetime.util.TimestampObject;
import util.PerformanceTest;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThoughputTimestampAnalysis17Test extends PerformanceTest {

	@Test
	public void testWithManyObjects() {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");

		// int count = 10;
		// while (count-- > 0) {
		final MethodCallThroughputAnalysis17 analysis = new MethodCallThroughputAnalysis17();
		analysis.setNumNoopFilters(NUM_NOOP_FILTERS);
		analysis.setInput(NUM_OBJECTS_TO_CREATE, new ConstructorClosure<TimestampObject>() {
			@Override
			public TimestampObject create() {
				return new TimestampObject();
			}
		});
		analysis.init();

		System.out.println("starting");
		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		this.timestampObjects = ListUtil.merge(analysis.getTimestampObjectsList());
		// }
	}
}
