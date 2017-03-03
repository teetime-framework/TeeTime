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
package teetime.framework;

import java.util.*;

class WatchTerminationThread extends Thread {

	private final List<AbstractStage> consumerStages;

	private volatile boolean shouldTerminate;

	public WatchTerminationThread() {
		consumerStages = Collections.synchronizedList(new ArrayList<>());
		setDaemon(true);
	}

	@Override
	public void run() {
		while (!shouldTerminate) {
			synchronized (consumerStages) {
				Iterator<AbstractStage> iterator = consumerStages.iterator();
				while (iterator.hasNext()) {
					AbstractStage stage = iterator.next();
					// FIXME remove <; so far, we use it for d&c
					if (stage.getNumOpenedInputPorts().get() <= 0 && stage.getCurrentState() == StageState.STARTED) {
						stage.terminateStageByFramework();
						stage.logger.debug("Terminated stage: " + stage);
						iterator.remove();
					}
				}
			}

			// if (consumerStages.isEmpty()) {
			// shouldTerminate = true;
			// break;
			// }

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				shouldTerminate = true;
			}
		}

	}

	public void addConsumerStage(final AbstractStage stage) {
		// only add consumers
		if (stage.getTerminationStrategy() == TerminationStrategy.BY_SIGNAL) {
			consumerStages.add(stage);
		}
	}

	// public void isShouldTerminate() {
	// shouldTerminate = true;
	// }
}
