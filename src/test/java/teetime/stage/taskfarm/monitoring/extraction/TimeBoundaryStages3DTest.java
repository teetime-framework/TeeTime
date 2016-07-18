/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class TimeBoundaryStages3DTest {

	@Ignore
	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryStages3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,stages");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,3");
		outputValues.add(",0.4,3");
		outputValues.add(",0.4,3");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Ignore
	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExporter extraction = new TimeBoundaryStages3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,stages"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
