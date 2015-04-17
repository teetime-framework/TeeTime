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
package teetime.framework.exceptionHandling;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.framework.AnalysisException;
import teetime.framework.StageState;

public class ExceptionHandlingTest {

	private Analysis<ExceptionTestConfiguration> analysis;

	public ExceptionTestConfiguration newInstances() {
		ExceptionTestConfiguration configuration = new ExceptionTestConfiguration();
		analysis = new Analysis<ExceptionTestConfiguration>(configuration, new TestListenerFactory());
		return configuration;
	}

	public void exceptionPassingAndTermination() {
		newInstances();
		analysis.executeBlocking();
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread too early
	}

	public void terminatesAllStages() {
		ExceptionTestConfiguration config = newInstances();
		analysis.executeBlocking();
		assertThat(config.first.getCurrentState(), is(StageState.TERMINATED));
		assertThat(config.second.getCurrentState(), is(StageState.TERMINATED));
		assertThat(config.third.getCurrentState(), is(StageState.TERMINATED));
	}

	@Test
	public void forAFewTimes() {
		for (int i = 0; i < 100; i++) {
			boolean exceptionArised = false;
			try {
				exceptionPassingAndTermination();
				terminatesAllStages();
			} catch (AnalysisException e) {
				exceptionArised = true;
			}
			assertTrue(exceptionArised);
		}
	}
}
