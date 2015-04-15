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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.Analysis;

public class ExceptionHandling {

	private Analysis analysis;

	// @Before
	public void newInstances() {
		analysis = new Analysis(new ExceptionTestConfiguration(), new TestListenerFactory());
	}

	// @Test(timeout = 5000, expected = RuntimeException.class)
	public void exceptionPassingAndTermination() {
		analysis.executeBlocking();
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread to early
	}

	@Test
	public void terminatesAllStages() {
		// TODO: more than one stage and check, if all are terminated (at least 3, each of every terminationtype)
		assertTrue(true);
	}

	/**
	 * If the consumer is terminated first while the pipe is full, the finite producer will be locked in
	 * SpScPipe.add and cycle through the sleep method. As a result, the thread will never return to the point
	 * where it checks if it should be terminated.
	 */
	@Test(timeout = 30000)
	public void forAFewTimes() {
		for (int i = 0; i < 1000; i++) {
			newInstances();
			try {
				exceptionPassingAndTermination();
			} catch (RuntimeException e) {
				// TODO: handle exception
			}
			System.out.println(i);
		}
	}
}
