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
package teetime.variant.methodcallWithPorts.examples.traceReconstructionWithThreads;

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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.util.ListUtil;
import teetime.util.StopWatch;
import teetime.variant.methodcallWithPorts.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import util.StatisticsUtil;

import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChwHomeTcpTraceReconstructionAnalysisWithThreadsTest {

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
	public void performAnalysisWith1Thread() {
		this.performAnalysis(1);
	}

	@Test
	public void performAnalysisWith2Threads() {
		this.performAnalysis(2);
	}

	@Test
	public void performAnalysisWith4Threads() {
		this.performAnalysis(4);
	}

	void performAnalysis(final int numWorkerThreads) {
		final TcpTraceReconstructionAnalysisWithThreadsConfiguration configuration = new TcpTraceReconstructionAnalysisWithThreadsConfiguration();
		configuration.setNumWorkerThreads(numWorkerThreads);
		configuration.buildConfiguration();

		Analysis analysis = new Analysis(configuration);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		int maxNumWaits = 0;
		for (SpScPipe<IMonitoringRecord> pipe : configuration.getTcpRelayPipes()) {
			maxNumWaits = Math.max(maxNumWaits, pipe.getNumWaits());
		}
		System.out.println("max #waits of TcpRelayPipes: " + maxNumWaits);

		// System.out.println("#traceMetadata read: " + analysis.getNumTraceMetadatas());
		// System.out.println("Max #trace created: " + analysis.getMaxElementsCreated());

		// Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(analysis.getRecordDelays());
		// System.out.println("Median record delay: " + recordQuintiles.get(0.5) + " time units/record");

		// Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(analysis.getTraceDelays());
		// System.out.println("Median trace delay: " + traceQuintiles.get(0.5) + " time units/trace");

		List<Long> recordThroughputs = ListUtil.removeFirstHalfElements(configuration.getRecordThroughputs());
		Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(recordThroughputs);
		System.out.println("Median record throughput: " + recordQuintiles.get(0.5) + " elements/time unit");

		// List<Long> traceThroughputs = ListUtil.removeFirstHalfElements(analysis.getTraceThroughputs());
		// Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(traceThroughputs);
		// System.out.println("Median trace throughput: " + traceQuintiles.get(0.5) + " traces/time unit");

		// TraceEventRecords trace6884 = analysis.getElementCollection().get(0);
		// assertEquals(6884, trace6884.getTraceMetadata().getTraceId());
		//
		// TraceEventRecords trace6886 = analysis.getElementCollection().get(1);
		// assertEquals(6886, trace6886.getTraceMetadata().getTraceId());

		assertEquals("#records", EXPECTED_NUM_RECORDS, configuration.getNumRecords());
		assertEquals("#traces", EXPECTED_NUM_TRACES, configuration.getNumTraces());

		for (Integer count : configuration.getNumTraceMetadatas()) {
			assertEquals("#traceMetadata per worker thread", EXPECTED_NUM_TRACES / numWorkerThreads, count.intValue()); // even distribution
		}

		// 08.07.2014 (incl.)
		assertThat(recordQuintiles.get(0.5), is(both(greaterThan(3100L)).and(lessThan(3500L))));
	}

	public static void main(final String[] args) {
		ChwHomeTcpTraceReconstructionAnalysisWithThreadsTest analysis = new ChwHomeTcpTraceReconstructionAnalysisWithThreadsTest();
		analysis.before();
		try {
			analysis.performAnalysisWith1Thread();
		} catch (Exception e) {
			System.err.println(e);
		}
		analysis.after();

		analysis.before();
		try {
			analysis.performAnalysisWith2Threads();
		} catch (Exception e) {
			System.err.println(e);
		}
		analysis.after();

		analysis.before();
		try {
			analysis.performAnalysisWith4Threads();
		} catch (Exception e) {
			System.err.println(e);
		}
		analysis.after();
	}

}
