/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.examples.experiment09pipeimpls;

import teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import teetime.util.test.eval.PerformanceResult;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.MeasurementRepository;
import util.test.PerformanceTest;

abstract class AbstractPerformanceCheck extends AbstractProfiledPerformanceAssertion {

	protected PerformanceResult test01;
	protected PerformanceResult test09CommittablePipes;
	protected PerformanceResult test09SingleElementPipes;
	protected PerformanceResult test09OrderedGrowableArrayPipes;

	@Override
	public void check() {
		String testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis1Test.class, "testWithManyObjects");
		test01 = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testCommittablePipes");
		test09CommittablePipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testSingleElementPipes");
		test09SingleElementPipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);

		testMethodIdentifier = MeasurementRepository.buildTestMethodIdentifier(MethodCallThoughputTimestampAnalysis9Test.class, "testOrderedGrowableArrayPipes");
		test09OrderedGrowableArrayPipes = PerformanceTest.measurementRepository.performanceResults.get(testMethodIdentifier);
	}

}
