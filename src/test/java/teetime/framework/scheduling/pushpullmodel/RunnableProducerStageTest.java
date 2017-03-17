/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.scheduling.pushpullmodel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.*;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.scheduling.pushpullmodel.RunnableProducerStage;

public class RunnableProducerStageTest {

	@Test(timeout = 1000)
	// t/o if join() waits infinitely
	public void testInit() throws InterruptedException {
		RunnableTestStage testStage = new RunnableTestStage();
		testStage.getOutputPort().setPipe(DummyPipe.INSTANCE);

		RunnableProducerStage runnable = new RunnableProducerStage(testStage);
		Thread thread = new Thread(runnable);

		testStage.setOwningThread(thread);
		testStage.setOwningContext(new ConfigurationContext(null));

		thread.start();

		// Not running, but initialized
		assertFalse(testStage.executed);
		runnable.triggerStartingSignal();

		thread.join();

		assertThat(testStage.getCurrentState(), is(StageState.TERMINATED));
		assertTrue(testStage.executed);
	}
}
