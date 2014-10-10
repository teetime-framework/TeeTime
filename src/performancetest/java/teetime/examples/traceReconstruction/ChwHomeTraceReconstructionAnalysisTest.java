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

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.util.StopWatch;
import util.StatisticsUtil;

import kieker.analysis.plugin.filter.flow.TraceEventRecords;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class ChwHomeTraceReconstructionAnalysisTest {

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
	public void performAnalysisWithEprintsLogs() {
		final TraceReconstructionAnalysis analysis = new TraceReconstructionAnalysis();
		analysis.setInputDir(new File("src/test/data/Eprints-logs"));
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		StatisticsUtil.removeLeadingZeroThroughputs(analysis.getThroughputs());
		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(analysis.getThroughputs());
		System.out.println("Median throughput: " + quintiles.get(0.5) + " elements/time unit");

		assertEquals(50002, analysis.getNumRecords());
		assertEquals(2, analysis.getNumTraces());

		TraceEventRecords trace6884 = analysis.getElementCollection().get(0);
		assertEquals(6884, trace6884.getTraceMetadata().getTraceId());

		TraceEventRecords trace6886 = analysis.getElementCollection().get(1);
		assertEquals(6886, trace6886.getTraceMetadata().getTraceId());

		assertThat(quintiles.get(0.5), is(both(greaterThan(34l)).and(lessThan(320l))));
	}

	@Test
	public void performAnalysisWithKiekerLogs() {
		final TraceReconstructionAnalysis analysis = new TraceReconstructionAnalysis();
		analysis.setInputDir(new File("src/test/data/kieker-logs"));
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		StatisticsUtil.removeLeadingZeroThroughputs(analysis.getThroughputs());
		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(analysis.getThroughputs());
		System.out.println("Median throughput: " + quintiles.get(0.5) + " elements/time unit");

		assertEquals(1489902, analysis.getNumRecords());
		assertEquals(24013, analysis.getNumTraces());

		TraceEventRecords trace0 = analysis.getElementCollection().get(0);
		assertEquals(8974347286117089280l, trace0.getTraceMetadata().getTraceId());

		TraceEventRecords trace1 = analysis.getElementCollection().get(1);
		assertEquals(8974347286117089281l, trace1.getTraceMetadata().getTraceId());

		assertThat(quintiles.get(0.5), is(both(greaterThan(1700l)).and(lessThan(1900l))));
	}

	@Test
	public void performAnalysisWithKieker2Logs() {
		final TraceReconstructionAnalysis analysis = new TraceReconstructionAnalysis();
		analysis.setInputDir(new File("src/test/data/kieker2-logs"));
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
			analysis.onTerminate();
		}

		StatisticsUtil.removeLeadingZeroThroughputs(analysis.getThroughputs());
		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(analysis.getThroughputs());
		System.out.println("Median throughput: " + quintiles.get(0.5) + " elements/time unit");

		assertEquals(17371, analysis.getNumRecords());
		assertEquals(22, analysis.getNumTraces());

		TraceEventRecords trace0 = analysis.getElementCollection().get(0);
		assertEquals(0, trace0.getTraceMetadata().getTraceId());

		TraceEventRecords trace1 = analysis.getElementCollection().get(1);
		assertEquals(1, trace1.getTraceMetadata().getTraceId());

		assertThat(quintiles.get(0.5), is(both(greaterThan(200l)).and(lessThan(250l))));
	}

}
