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
import org.junit.Test;

import teetime.util.StatisticsUtil;
import teetime.util.StopWatch;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
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
	public void performAnalysis() {
		final TcpTraceReconstructionAnalysisWithThreads analysis = new TcpTraceReconstructionAnalysisWithThreads();
		analysis.setNumWorkerThreads(2);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		System.out.println("Max size of tcp-relay pipe: " + analysis.getTcpRelayPipe().getMaxSize());

		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(analysis.getTraceThroughputs());
		System.out.println("Median throughput: " + quintiles.get(0.5) + " elements/ms");

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

}
