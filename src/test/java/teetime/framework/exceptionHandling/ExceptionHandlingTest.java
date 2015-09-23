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

import org.junit.Ignore;
import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.ExecutionException;

public class ExceptionHandlingTest {

	private Execution<ExceptionTestConfiguration> execution;

	public ExceptionTestConfiguration newInstances() {
		ExceptionTestConfiguration configuration = new ExceptionTestConfiguration();
		execution = new Execution<ExceptionTestConfiguration>(configuration);
		return configuration;
	}

	public void exceptionPassingAndTermination() {
		newInstances();
		execution.executeBlocking();
		fail(); // Should never be executed
	}

	public void terminatesAllStages() {
		ExceptionTestConfiguration config = newInstances();
		execution.executeBlocking();
		fail(); // Should never be executed
	}

	@Test
	public void testException() {
		boolean exceptionArised = false;
		ExceptionPassingTestConfig exceptionPassingTestConfig = new ExceptionPassingTestConfig();
		try {
			new Execution<ExceptionPassingTestConfig>(exceptionPassingTestConfig).executeBlocking();
		} catch (ExecutionException e) {
			exceptionArised = true;
		}
		assertTrue(exceptionArised);
	}

	@Ignore
	@Test
	public void forAFewTimes() {
		for (int i = 0; i < 100; i++) {
			boolean exceptionArised = false;
			try {
				exceptionPassingAndTermination(); // listener did not kill thread too early;
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
}
