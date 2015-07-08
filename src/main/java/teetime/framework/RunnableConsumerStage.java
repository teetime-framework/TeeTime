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

import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

public final class RunnableConsumerStage extends AbstractRunnableStage {

	/**
	 * Creates a new instance.
	 *
	 * @param stage
	 *            to execute within an own thread
	 */
	public RunnableConsumerStage(final Stage stage) {
		super(stage);
	}

	@SuppressWarnings("PMD.GuardLogStatement")
	@Override
	protected void beforeStageExecution() throws InterruptedException {
		logger.trace("Waiting for init signals... " + stage);
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			inputPort.waitForInitializingSignal();
		}
		logger.trace("Waiting for start signals... " + stage);
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			inputPort.waitForStartSignal();
		}
		logger.trace("Starting... " + stage);
	}

	@Override
	protected void executeStage() {
		try {
			stage.executeStage();
		} catch (NotEnoughInputException e) {
			checkForTerminationSignal(stage);
		}
	}

	private void checkForTerminationSignal(final Stage stage) {
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			// System.out.println("\tclosed: " + inputPort.isClosed() + " (" + inputPort);
			if (!inputPort.isClosed()) {
				return;
			}
		}
		// System.out.println("checkForTerminationSignal: " + stage);
		// FIXME should getInputPorts() really be defined in Stage?

		stage.terminate();
	}

	@Override
	protected void afterStageExecution() {
		final ISignal signal = new TerminatingSignal();
		for (InputPort<?> inputPort : stage.getInputPorts()) {
			stage.onSignal(signal, inputPort);
		}
	}

}
