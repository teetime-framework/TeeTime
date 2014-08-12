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
public class ChwWorkTcpTraceReconstructionAnalysisWithThreadsTest {

	private static final int MIO = 1000000;
	private static final int EXPECTED_NUM_TRACES = 1 * MIO;

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

	// until 04.07.2014 (incl.)
	// Max size of tcp-relay pipe: 143560
	// Median trace throughput: 115 traces/time unit
	// Duration: 12907 ms

	// Max size of tcp-relay pipe: 51948
	// Median trace throughput: 42 traces/time unit
	// Duration: 21614 ms

	// [2014-07-04 01:06:10 PM] WARNUNG: Reader interrupted (teetime.variant.methodcallWithPorts.stage.io.TCPReader$TCPStringReader run)
	// Max size of tcp-relay pipe: 167758
	// Median trace throughput: 6 traces/time unit
	// Duration: 22373 ms

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

		List<Long> traceThroughputs = ListUtil.removeFirstHalfElements(configuration.getTraceThroughputs());
		Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(traceThroughputs);
		System.out.println("Median trace throughput: " + traceQuintiles.get(0.5) + " traces/time unit");

		// TraceEventRecords trace6884 = analysis.getElementCollection().get(0);
		// assertEquals(6884, trace6884.getTraceMetadata().getTraceId());
		//
		// TraceEventRecords trace6886 = analysis.getElementCollection().get(1);
		// assertEquals(6886, trace6886.getTraceMetadata().getTraceId());

		assertEquals("#records", 21000001, configuration.getNumRecords());

		for (Integer count : configuration.getNumTraceMetadatas()) {
			assertEquals("#traceMetadata per worker thread", EXPECTED_NUM_TRACES / numWorkerThreads, count.intValue()); // even distribution
		}

		assertEquals("#traces", EXPECTED_NUM_TRACES, configuration.getNumTraces());
	}

	public static void main(final String[] args) {
		ChwWorkTcpTraceReconstructionAnalysisWithThreadsTest analysis = new ChwWorkTcpTraceReconstructionAnalysisWithThreadsTest();
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
