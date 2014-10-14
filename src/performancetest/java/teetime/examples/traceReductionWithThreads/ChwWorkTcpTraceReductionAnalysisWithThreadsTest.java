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
package teetime.examples.traceReductionWithThreads;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import teetime.util.ListUtil;
import teetime.util.StopWatch;
import util.MooBenchStarter;
import util.StatisticsUtil;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChwWorkTcpTraceReductionAnalysisWithThreadsTest {

	private static final int EXPECTED_NUM_TRACES = 1000000;
	private static final int EXPECTED_NUM_SAME_TRACES = 1;

	private static MooBenchStarter mooBenchStarter;

	private StopWatch stopWatch;

	@BeforeClass
	public static void beforeClass() {
		mooBenchStarter = new MooBenchStarter();
	}

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
	public void performAnalysisWith1Thread() throws InterruptedException, IOException {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ChwWorkTcpTraceReductionAnalysisWithThreadsTest.this.performAnalysis(1);
			}
		};
		this.startTest(runnable);
	}

	@Test
	public void performAnalysisWith2Threads() throws InterruptedException, IOException {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ChwWorkTcpTraceReductionAnalysisWithThreadsTest.this.performAnalysis(2);
			}
		};
		this.startTest(runnable);
	}

	@Test
	public void performAnalysisWith4Threads() throws InterruptedException, IOException {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ChwWorkTcpTraceReductionAnalysisWithThreadsTest.this.performAnalysis(4);
			}
		};
		this.startTest(runnable);
	}

	void performAnalysis(final int numWorkerThreads) {
		final TcpTraceReductionAnalysisWithThreads analysis = new TcpTraceReductionAnalysisWithThreads();
		analysis.setNumWorkerThreads(numWorkerThreads);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		System.out.println("#waits of tcp-relay pipe: " + analysis.getTcpRelayPipe().getNumWaits());
		// System.out.println("#traceMetadata read: " + analysis.getNumTraceMetadatas());
		// System.out.println("Max #trace created: " + analysis.getMaxElementsCreated());
		System.out.println("TraceThroughputs: " + analysis.getTraceThroughputs());

		// Map<Double, Long> recordQuintiles = StatisticsUtil.calculateQuintiles(analysis.getRecordDelays());
		// System.out.println("Median record delay: " + recordQuintiles.get(0.5) + " time units/record");

		// Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(analysis.getTraceDelays());
		// System.out.println("Median trace delay: " + traceQuintiles.get(0.5) + " time units/trace");

		List<Long> traceThroughputs = ListUtil.removeFirstHalfElements(analysis.getTraceThroughputs());
		Map<Double, Long> traceQuintiles = StatisticsUtil.calculateQuintiles(traceThroughputs);
		System.out.println("Median trace throughput: " + traceQuintiles.get(0.5) + " traces/time unit");

		assertEquals("#records", 21000001, analysis.getNumRecords());

		for (Integer count : analysis.getNumTraceMetadatas()) {
			assertEquals("#traceMetadata per worker thread", EXPECTED_NUM_TRACES / numWorkerThreads, count.intValue()); // even distribution
		}

		assertEquals("#traces", EXPECTED_NUM_SAME_TRACES, analysis.getNumTraces());
	}

	private void startTest(final Runnable runnable) throws InterruptedException, IOException {
		Thread thread = new Thread(runnable);
		thread.start();

		Thread.sleep(1000);

		mooBenchStarter.start(1, EXPECTED_NUM_TRACES);

		thread.join();
	}

	public static void main(final String[] args) {
		ChwWorkTcpTraceReductionAnalysisWithThreadsTest analysis = new ChwWorkTcpTraceReductionAnalysisWithThreadsTest();
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
