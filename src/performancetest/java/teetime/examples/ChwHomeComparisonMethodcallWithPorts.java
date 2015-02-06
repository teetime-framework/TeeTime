/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.examples;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceResult;
import util.test.PerformanceTest;

public class ChwHomeComparisonMethodcallWithPorts extends AbstractProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return HostName.CHW_HOME.toString();
	}

	@Override
	public void check() {
		Map<String, PerformanceResult> performanceResults = PerformanceTest.measurementRepository.performanceResults;
		for (Entry<String, PerformanceResult> entry : performanceResults.entrySet()) {
			System.out.println("---> " + entry.getKey() + "\n" + entry.getValue());
		}

		PerformanceResult test1 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test15 = performanceResults
				.get("testWithManyObjects(teetime.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test)");
		PerformanceResult test19a = performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19b = performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");
		PerformanceResult test19c = performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test)");

		double value15 = (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5);

		System.out.println("value15: " + value15);

		// until 25.06.2014 (incl.)
		// assertEquals(44, (double) test15.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		// assertEquals(39, (double) test17.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);

		// since 26.06.2014 (incl.)
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +14

		// // since 04.07.2014 (incl.)
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +0

		// since 11.08.2014 (incl.)
		// assertEquals(44, value15, 4.1); // +0
		// assertEquals(53, value17, 4.1); // +0

		// since 31.08.2014 (incl.)
		// assertEquals(68, value15, 4.1); // +24
		// assertEquals(75, value17, 4.1); // +22

		// since 04.11.2014 (incl.)
		// assertEquals(40, value15, 4.1); // -28
		// assertEquals(78, value17, 4.1); // +3

		// since 13.12.2014 (incl.)
		// assertEquals(40, value15, 4.1); // -28
		// assertEquals(43, value17, 4.1); // -35

		// since 28.12.2014 (incl.)
		assertEquals(30, value15, 4.1); // -10

		// check speedup
		assertEquals(2, (double) test19a.overallDurationInNs / test19b.overallDurationInNs, 0.3);
		assertEquals(2, (double) test19b.overallDurationInNs / test19c.overallDurationInNs, 0.3);
	}

}
