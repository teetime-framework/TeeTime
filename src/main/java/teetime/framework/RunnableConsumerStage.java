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

import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

final class RunnableConsumerStage extends AbstractRunnableStage {

	/**
	 * Creates a new instance.
	 *
	 * @param stage
	 *            to execute within an own thread
	 */
	public RunnableConsumerStage(final AbstractStage stage) {
		super(stage);
	}

	@Override
	protected void beforeStageExecution() throws InterruptedException {
		logger.trace("waitForStartingSignal");
		// FIXME should getInputPorts() really be defined in Stage?
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			inputPort.waitForStartSignal();
		}

		// if the producers have closed all input ports before changing the state to STARTED,
		// they do not set the state to TERMINATING.
		// Hence, the stage's state needs to be set at this position.
		// if (stage.getNumOpenedInputPorts().get() == 0) {
		// stage.terminateStage();
		// }
	}

	@Override
	protected void afterStageExecution() {
		// stage.terminateStage(); // change state to terminating

		logger.debug("Removing remaining elements...");
		try {
			while (hasRemainingElements()) {
				stage.executeStage();
			}
		} catch (TerminateException ignore) {// NOPMD
			// ignore exception since we cannot do anything here.
			// However, we must pass the termination signal
		}

		final ISignal signal = new TerminatingSignal(); // NOPMD DU caused by loop
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			stage.onSignal(signal, inputPort);
		}
	}

	private boolean hasRemainingElements() {
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			if (inputPort.getPipe().hasMore()) {
				return true;
			}
		}
		return false;
	}

}
