/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static teetime.testutil.AssertHelper.assertInstanceOf;

import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import teetime.framework.Execution;
import teetime.framework.ExecutionException;

public class ExceptionHandlingTest {

	@Test
	public void testException() {
		Execution<ExceptionPassingTestConfig> execution = new Execution<ExceptionPassingTestConfig>(new ExceptionPassingTestConfig());
		try {
			execution.executeBlocking();
		} catch (ExecutionException e) {
			Entry<Thread, List<Exception>> entry = e.getThrownExceptions().entrySet().iterator().next();
			List<Exception> exceptions = entry.getValue();
			IllegalStateException exception = assertInstanceOf(IllegalStateException.class, exceptions.get(0));
			assertThat(exception.getMessage(), is(equalTo("Correct exception")));

			assertThat(exceptions.size(), is(1));
			assertThat(e.getThrownExceptions().size(), is(1));
		}
	}

}
