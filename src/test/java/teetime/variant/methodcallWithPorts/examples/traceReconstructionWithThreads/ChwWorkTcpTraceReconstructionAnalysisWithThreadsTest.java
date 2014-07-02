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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChwWorkTcpTraceReconstructionAnalysisWithThreadsTest {

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
		final TcpTraceReconstructionAnalysisWithThreads analysis = new TcpTraceReconstructionAnalysisWithThreads();
		analysis.setNumWorkerThreads(numWorkerThreads);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		System.out.println("Max size of tcp-relay pipe: " + analysis.getTcpRelayPipe().getMaxSize());

		// Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(analysis.getRecordDelays());
		// System.out.println("Median record delay: " + recordQuintiles.get(0.5) + " time units/record");

		// Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(analysis.getTraceDelays());
		// System.out.println("Median trace delay: " + traceQuintiles.get(0.5) + " time units/trace");
		Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(analysis.getTraceThroughputs());
		System.out.println("Median trace throughput: " + traceQuintiles.get(0.5) + " traces/time unit");

		// assertEquals(1000, analysis.getNumTraces());
		assertEquals(1000000, analysis.getNumTraces());

		// TraceEventRecords trace6884 = analysis.getElementCollection().get(0);
		// assertEquals(6884, trace6884.getTraceMetadata().getTraceId());
		//
		// TraceEventRecords trace6886 = analysis.getElementCollection().get(1);
		// assertEquals(6886, trace6886.getTraceMetadata().getTraceId());

		// assertEquals(21001, analysis.getNumRecords());
		assertEquals(21000001, analysis.getNumRecords());
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