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
package teetime.variant.methodcallWithPorts.examples.traceReading;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.util.ListUtil;
import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChwHomeTcpTraceReadingTest {

	private static final int MIO = 1000000;
	private static final int EXPECTED_NUM_TRACES = 10 * MIO;
	private static final int EXPECTED_NUM_RECORDS = 21 * EXPECTED_NUM_TRACES + 1;

	private StopWatch stopWatch;

	@Before
	public void before() {
		this.stopWatch = new StopWatch();
	}

	@After
	public void after() {
		long overallDurationInNs = this.stopWatch.getDurationInNs();
		System.out.println("Duration: " + TimeUnit.NANOSECONDS.toMillis(overallDurationInNs) + " ms");
	}

	@Test
	public void performAnalysis() {
		final TcpTraceLoggingExtAnalysis analysis = new TcpTraceLoggingExtAnalysis();
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		List<Long> recordThroughputs = ListUtil.removeFirstHalfElements(analysis.getRecordThroughputs());
		Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(recordThroughputs);
		System.out.println("Median record throughput: " + recordQuintiles.get(0.5) + " records/time unit");

		assertEquals("#records", EXPECTED_NUM_RECORDS, analysis.getNumRecords());
	}

}
