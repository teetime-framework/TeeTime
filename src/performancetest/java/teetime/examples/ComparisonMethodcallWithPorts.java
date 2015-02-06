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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import teetime.examples.experiment01.MethodCallThoughputTimestampAnalysis1Test;
import teetime.examples.experiment09pipeimpls.MethodCallThoughputTimestampAnalysis9Test;
import teetime.examples.experiment11.MethodCallThoughputTimestampAnalysis11Test;
import teetime.examples.experiment15.MethodCallThoughputTimestampAnalysis15Test;
import teetime.examples.experiment16.MethodCallThoughputTimestampAnalysis16Test;
import teetime.examples.experiment19.MethodCallThoughputTimestampAnalysis19Test;
import util.test.AbstractProfiledPerformanceAssertion;
import util.test.PerformanceCheckProfileRepository;

@RunWith(Suite.class)
@SuiteClasses({
	MethodCallThoughputTimestampAnalysis1Test.class,
	MethodCallThoughputTimestampAnalysis9Test.class,
	MethodCallThoughputTimestampAnalysis11Test.class,
	MethodCallThoughputTimestampAnalysis15Test.class,
	MethodCallThoughputTimestampAnalysis16Test.class,
	MethodCallThoughputTimestampAnalysis19Test.class,
})
public class ComparisonMethodcallWithPorts {

	@BeforeClass
	public static void beforeClass() {
		// System.setProperty("logback.configurationFile", "src/test/resources/logback.groovy");
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new ChwWorkComparisonMethodcallWithPorts());
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new ChwHomeComparisonMethodcallWithPorts());
		PerformanceCheckProfileRepository.INSTANCE.register(ComparisonMethodcallWithPorts.class, new NieWorkComparisonMethodcallWithPorts());
	};

	@AfterClass
	public static void compareResults() {
		AbstractProfiledPerformanceAssertion pcp = PerformanceCheckProfileRepository.INSTANCE.get(ComparisonMethodcallWithPorts.class);
		pcp.check();
	}

}
