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
package teetime.examples.traceReconstruction;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.util.ListUtil;
import teetime.util.StopWatch;
import util.test.StatisticsUtil;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class ChwHomeTcpTraceReconstructionAnalysisTest {

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
		final TcpTraceReconstructionAnalysis analysis = new TcpTraceReconstructionAnalysis();
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		List<Long> recordThroughputs = ListUtil.removeFirstHalfElements(analysis.getRecordThroughputs());
		Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(recordThroughputs);
		System.out.println("Median record throughput: " + recordQuintiles.get(0.5) + " elements/time unit");

		// List<Long> traceThroughputs = ListUtil.removeFirstHalfElements(analysis.getTraceThroughputs());
		// Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(traceThroughputs);
		// System.out.println("Median trace throughput: " + traceQuintiles.get(0.5) + " traces/time unit");

		assertEquals("#records", EXPECTED_NUM_RECORDS, analysis.getNumRecords());
		assertEquals("#traces", EXPECTED_NUM_TRACES, analysis.getNumTraces());

		// TraceEventRecords trace6884 = analysis.getElementCollection().get(0);
		// assertEquals(6884, trace6884.getTraceMetadata().getTraceId());
		//
		// TraceEventRecords trace6886 = analysis.getElementCollection().get(1);
		// assertEquals(6886, trace6886.getTraceMetadata().getTraceId());

		// until 04.07.2014 (incl.)
		// Median throughput: 74 elements/time unit
		// Duration: 17445 ms
		// Median throughput: 78 elements/time unit
		// Duration: 16608 ms

		// 08.07.2014 (incl.)
		assertThat(recordQuintiles.get(0.5), is(both(greaterThan(3000L)).and(lessThan(3200L))));
	}
}
