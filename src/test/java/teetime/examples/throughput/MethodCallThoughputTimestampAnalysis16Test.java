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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import teetime.examples.throughput.methodcall.Closure;
import teetime.examples.throughput.methodcall.MethodCallThroughputAnalysis16;
import teetime.util.ListUtil;
import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;

import kieker.common.logging.LogFactory;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallThoughputTimestampAnalysis16Test {

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

		final MethodCallThroughputAnalysis16 analysis = new MethodCallThroughputAnalysis16();
		analysis.setNumNoopFilters(NUM_NOOP_FILTERS);
		analysis.setInput(NUM_OBJECTS_TO_CREATE, new Closure<Void, TimestampObject>() {
			@Override
			public TimestampObject execute(final Void element) {
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

		List<TimestampObject> timestampObjects = ListUtil.merge(analysis.getTimestampObjectsList());
		StatisticsUtil.printStatistics(stopWatch.getDurationInNs(), timestampObjects);
	}

	public static void main(final String[] args) {
		MethodCallThoughputTimestampAnalysis16Test test = new MethodCallThoughputTimestampAnalysis16Test();
		test.before();
		test.testWithManyObjects();
	}
}
