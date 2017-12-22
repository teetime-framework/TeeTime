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
package teetime.framework.pipe.strategy;

import teetime.framework.StageState;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.pipe.IPipe;

public class SleepIfFullStrategy implements PipeElementInsertionStrategy {

	// statistics
	private int numWaits;

	@Override
	public void add(final IPipe<?> pipe, final Object element) {
		while (!pipe.addNonBlocking(element)) {
			// the following sending*-related lines are commented out since they are computationally too expensive
			// this.getSourcePort().getOwningStage().sendingFailed();
			// Thread.yield();
			StageState targetStageState = pipe.getTargetPort().getOwningStage().getCurrentState();
			if (targetStageState == StageState.TERMINATED ||
					Thread.currentThread().isInterrupted()) {
				throw TerminateException.INSTANCE;
			}
			this.numWaits++;
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) { // NOPMD can be interrupted w/o any reason
				throw TerminateException.INSTANCE;
			}
		}
		// this.getSourcePort().getOwningStage().sendingSucceeded();
		// this.reportNewElement();
	}

	public int getNumWaits() {
		return numWaits;
	}

}
