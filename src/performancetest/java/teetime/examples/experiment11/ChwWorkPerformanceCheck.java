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
package teetime.examples.experiment11;

import static org.junit.Assert.assertEquals;
import teetime.util.test.eval.PerformanceResult;
import util.test.PerformanceTest;
import util.test.AbstractProfiledPerformanceAssertion;

class ChwWorkPerformanceCheck extends AbstractProfiledPerformanceAssertion {

	@Override
	public String getCorrespondingPerformanceProfile() {
		return "ChwWork";
	}

	@Override
	public void check() {
		PerformanceResult test01 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test)");
		PerformanceResult test11 = PerformanceTest.measurementRepository.performanceResults
				.get("testWithManyObjects(teetime.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test)");

		double medianSpeedup = (double) test11.quantiles.get(0.5) / test01.quantiles.get(0.5);

		System.out.println("medianSpeedup (11): " + medianSpeedup);

		// until 25.06.2014 (incl.)
		// assertEquals(32, (double) test11.quantiles.get(0.5) / test1.quantiles.get(0.5), 4.1);
		// since 26.06.2014 (incl.)
		// assertEquals(44, medianSpeedup, 4.1); // +12
		// since 04.07.2014 (incl.)
		// assertEquals(41, medianSpeedup, 4.1); // -3
		// since 27.08.2014 (incl.)
		// assertEquals(64, medianSpeedup, 4.1); // +15
		// since 14.10.2014 (incl.)
		assertEquals(44, medianSpeedup, 4.1); // -20
	}
}
