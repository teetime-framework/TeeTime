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

public class TimeBoundaryMSPullThroughput3DTest {

	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryMSPullThroughput3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,mpullthroughput,pullthroughputsum");
		outputValues.add(",0.4,6.0,16.0");
		outputValues.add(",0.4,26.0,36.0");
		outputValues.add(",0.4,46.0,56.0");
		outputValues.add(",0.4," + (66.0 + 6.0) / 2.0 + ",92.0");
		outputValues.add(",0.4," + (86.0 + 26.0) / 2.0 + ",132.0");
		outputValues.add(",0.4," + (106.0 + 46.0) / 2.0 + ",172.0");
		outputValues.add(",0.4," + (126.0 + 66.0 + 6.0) / 3.0 + ",228.0");
		outputValues.add(",0.4," + (146.0 + 86.0 + 26.0) / 3.0 + ",288.0");
		outputValues.add(",0.4," + (166.0 + 106.0 + 46.0) / 3.0 + ",348.0");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryMSPullThroughput3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,mpullthroughput,pullthroughputsum"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
