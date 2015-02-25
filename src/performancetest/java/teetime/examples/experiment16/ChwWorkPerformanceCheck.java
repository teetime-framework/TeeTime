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
package teetime.examples.experiment16;

import static org.junit.Assert.assertEquals;
import teetime.util.test.eval.PerformanceResult;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceTest;

class ChwWorkPerformanceCheck extends AbstractProfiledPerformanceAssertion {

	@Override
	public void check() {
		PerformanceResult test16a = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd1Thread(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16b = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd2Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		PerformanceResult test16c = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd4Threads(teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test)");
		// check speedup
		double speedupB = (double) test16a.overallDurationInNs / test16b.overallDurationInNs;
		double speedupC = (double) test16a.overallDurationInNs / test16c.overallDurationInNs;

		System.out.println("speedupB: " + speedupB);
		System.out.println("speedupC: " + speedupC);

		assertEquals(2, speedupB, 0.3);
		// assertEquals(2.5, speedupC, 0.3);
		// since 19.12.2014
		assertEquals(2.0, speedupC, 0.3);
	}

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}
}
