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

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;

public class StackedTimePullThroughput2DTest {

	@Test
	public void testWorkingService() {
		PipeMonitoringService service = ExtractorTestHelper.generate4PipeMonitoringServiceWithBehavior();

		AbstractMonitoringDataExporter extraction = new StackedTimePullThroughput2D(service, null);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,pullthroughput0,pullthroughput1,pullthroughput2,pullthroughput3");
		// as the exact processing time is slightly nondeterministic, we only check for the other values
		outputValues.add(",6,0,0,0");
		outputValues.add(",11,0,0,0");
		outputValues.add(",16,6,0,0");
		outputValues.add(",21,0,6,0");
		outputValues.add(",26,0,11,0");
		outputValues.add(",31,0,0,0");
		outputValues.add(",36,0,0,6");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		PipeMonitoringService service = ExtractorTestHelper.generateEmpty5PipeMonitoringService();

		AbstractMonitoringDataExporter extraction = new StackedTimePullThroughput2D(service, null);
		String result = extraction.extractToString();

		String header = "time,pullthroughput0,pullthroughput1,pullthroughput2,pullthroughput3,pullthroughput4"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}
}
