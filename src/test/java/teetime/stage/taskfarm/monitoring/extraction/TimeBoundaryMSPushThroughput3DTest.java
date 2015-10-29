/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class TimeBoundaryMSPushThroughput3DTest {

	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryMSPushThroughput3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,mpushthroughput,pushthroughputsum");
		outputValues.add(",0.4,7.0,13.0");
		outputValues.add(",0.4,19.0,25.0");
		outputValues.add(",0.4,31.0,37.0");
		outputValues.add(",0.4," + (43.0 + 7.0) / 2.0 + ",62.0");
		outputValues.add(",0.4," + (55.0 + 19.0) / 2.0 + ",86.0");
		outputValues.add(",0.4," + (67.0 + 31.0) / 2.0 + ",110.0");
		outputValues.add(",0.4," + (79.0 + 43.0 + 7.0) / 3.0 + ",147.0");
		outputValues.add(",0.4," + (91.0 + 55.0 + 19.0) / 3.0 + ",183.0");
		outputValues.add(",0.4," + (103.0 + 67.0 + 31.0) / 3.0 + ",219.0");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryMSPushThroughput3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,mpushthroughput,pushthroughputsum"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
