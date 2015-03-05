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
import teetime.examples.HostName;
import teetime.util.test.eval.PerformanceResult;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceTest;

class ChwHomePerformanceCheck extends AbstractProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return HostName.CHW_HOME.toString();
	}

	@Override
	public void check() {
		PerformanceResult test16a = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd1Thread(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		PerformanceResult test16b = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd2Threads(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		PerformanceResult test16c = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjectsAnd4Threads(" + MethodCallThoughputTimestampAnalysis16Test.class.getName() + ")");
		// check speedup
		double speedupA2B = (double) test16a.overallDurationInNs / test16b.overallDurationInNs;
		double speedupB2C = (double) test16b.overallDurationInNs / test16c.overallDurationInNs;

		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupB: " + speedupA2B);
		System.out.println(ChwHomePerformanceCheck.class.getName() + ", speedupC: " + speedupB2C);

		// assertEquals(2, speedupB, 0.3);
		// since 31.08.2014 (incl.)
		// assertEquals(3.6, speedupC, 0.3);
		// since 04.11.2014 (incl.)
		// assertEquals(5, speedupC, 0.4);
		// since 07.12.2014 (incl.)
		// assertEquals(2, speedupA2B, 0.4);
		// assertEquals(5, speedupB2C, 0.4);
		// since 28.12.2014 (incl.)
		assertEquals(2, speedupA2B, 0.4);
		assertEquals(2, speedupB2C, 0.4);
	}
}
