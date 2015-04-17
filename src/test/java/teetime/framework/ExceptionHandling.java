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
package teetime.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import teetime.framework.exceptionHandling.TestListener;

public class ExceptionHandling {

	private Class<TestListener> listener;
	private Analysis analysis;

	@Before
	public void newInstances() {
		listener = TestListener.class;
		analysis = new Analysis(new ExceptionTestConfiguration(), listener);
	}

	@Test(timeout = 5000, expected = RuntimeException.class)
	public void exceptionPassingAndTermination() {
		analysis.execute();
		assertEquals(TestListener.exceptionInvoked, 2); // listener did not kill thread to early
	}

	@Test
	public void terminatesAllStages() {
		// TODO: more than one stage and check, if all are terminated (at least 3, each of every terminationtype)
		assertTrue(true);
	}
}
