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
package teetime.examples.throughput;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import teetime.examples.throughput.methodcall.MethodCallThroughputAnalysis17;
import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;

import kieker.common.logging.LogFactory;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallThoughputTimestampAnalysis17Test {

	private static final int NUM_OBJECTS_TO_CREATE = 100000;
	private static final int NUM_NOOP_FILTERS = 800;

	@Before
	public void before() {
		System.setProperty(LogFactory.CUSTOM_LOGGER_JVM, "NONE");
	}

	@Test
	public void testWithManyObjects() {
		System.out.println("Testing teetime (mc) with NUM_OBJECTS_TO_CREATE=" + NUM_OBJECTS_TO_CREATE + ", NUM_NOOP_FILTERS="
				+ NUM_NOOP_FILTERS + "...");
		final StopWatch stopWatch = new StopWatch();

		final MethodCallThroughputAnalysis17 analysis = new MethodCallThroughputAnalysis17();
		analysis.setNumNoopFilters(NUM_NOOP_FILTERS);
		analysis.setInput(NUM_OBJECTS_TO_CREATE, new Callable<TimestampObject>() {
			@Override
			public TimestampObject call() throws Exception {
				return new TimestampObject();
			}
		});
		analysis.init();

		stopWatch.start();
		try {
			analysis.start();
		} finally {
			stopWatch.end();
			analysis.onTerminate();
		}

		// merge
		List<TimestampObject> timestampObjects = analysis.getTimestampObjectsList().get(0);
		for (int i = 1; i < analysis.getTimestampObjectsList().size(); i++) {
			Collection<? extends TimestampObject> timestampObjectList = analysis.getTimestampObjectsList().get(i);
			timestampObjects.addAll(timestampObjectList);
		}
		StatisticsUtil.printStatistics(stopWatch.getDurationInNs(), timestampObjects);
	}
}
