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
package teetime.examples.recordReader;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teetime.framework.Analysis;
import teetime.util.StopWatch;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.misc.KiekerMetadataRecord;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class RecordReaderAnalysisTest {

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
		final RecordReaderConfiguration configuration = new RecordReaderConfiguration();

		Analysis analysis = new Analysis(configuration);
		analysis.init();

		this.stopWatch.start();
		try {
			analysis.start();
		} finally {
			this.stopWatch.end();
		}

		assertEquals(6541, configuration.getElementCollection().size());

		KiekerMetadataRecord metadataRecord = (KiekerMetadataRecord) configuration.getElementCollection().get(0);
		assertEquals("1.9-SNAPSHOT", metadataRecord.getVersion());
		assertEquals("NANOSECONDS", metadataRecord.getTimeUnit());

		IMonitoringRecord monitoringRecord = configuration.getElementCollection().get(1);
		OperationExecutionRecord oer = (OperationExecutionRecord) monitoringRecord;
		assertEquals("bookstoreTracing.Catalog.getBook(boolean)", oer.getOperationSignature());
		assertEquals(1283156498771185344l, oer.getTin());
		assertEquals(1283156498773323582l, oer.getTout());

		monitoringRecord = configuration.getElementCollection().get(configuration.getElementCollection().size() - 1);
		oer = (OperationExecutionRecord) monitoringRecord;
		assertEquals("bookstoreTracing.Bookstore.searchBook()", oer.getOperationSignature());
		assertEquals(1283156499331233504l, oer.getTin());
		assertEquals(1283156499363031606l, oer.getTout());
	}

}
