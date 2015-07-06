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
package teetime.framework.exceptionHandling;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.ExecutionException;

public class ExceptionHandlingTest {

	@Test
	public void forAFewTimes() {
		for (int i = 0; i < 100; i++) {
			boolean exceptionArised = false;
			try {
				exceptionPassingAndTermination();
			} catch (ExecutionException e) {
				exceptionArised = true;
			}
			assertTrue(exceptionArised);

			exceptionArised = false;
			try {
				terminatesAllStages();
			} catch (ExecutionException e) {
				exceptionArised = true;
			}
			assertTrue(exceptionArised);
		}
	}

	public void exceptionPassingAndTermination() {
		Execution<ExceptionTestConfiguration> execution = newInstances();

		execution.executeBlocking();

		fail();
		// TestListenerFactory factory = (TestListenerFactory) execution.getFactory();
		// assertThat(factory.getInstances(), hasSize(2));
		// assertEquals(factory.getInstances().get(0).getNumExceptionsInvoked(), 2); // listener did not kill thread too early
	}

	public Execution<ExceptionTestConfiguration> newInstances() {
		ExceptionTestConfiguration configuration = new ExceptionTestConfiguration();
		TestListenerFactory factory = new TestListenerFactory();
		return new Execution<ExceptionTestConfiguration>(configuration, factory);
	}

	public void terminatesAllStages() {
		Execution<ExceptionTestConfiguration> execution = newInstances();

		execution.executeBlocking();

		fail();
		// ExceptionTestConfiguration config = execution.getConfiguration();
		// assertThat(config.first.getCurrentState(), is(StageState.TERMINATED));
		// assertThat(config.second.getCurrentState(), is(StageState.TERMINATED));
		// assertThat(config.third.getCurrentState(), is(StageState.TERMINATED));
	}
}
