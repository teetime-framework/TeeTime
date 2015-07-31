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
package teetime.framework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teetime.framework.pipe.DummyPipe;

public class RunnableProducerStageTest {

	@Test
	public void testInit() {
		RunnableTestStage testStage = new RunnableTestStage();
		testStage.getOutputPort().setPipe(DummyPipe.INSTANCE);
		RunnableProducerStage runnable = new RunnableProducerStage(testStage);
		Thread thread = new Thread(runnable);
		thread.start();
		// Not running and not initialized
		assertFalse(testStage.executed && testStage.initialized);
		runnable.triggerInitializingSignal();
		// Not running, but initialized
		assertFalse(testStage.executed && !testStage.initialized);
		runnable.triggerStartingSignal();
		while (!(testStage.getCurrentState() == StageState.TERMINATED)) {
			Thread.yield();
		}
		assertTrue(testStage.executed);
	}
}
