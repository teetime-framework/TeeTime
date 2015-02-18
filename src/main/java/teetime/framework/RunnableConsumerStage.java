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

import java.util.Arrays;

import teetime.framework.idle.IdleStrategy;
import teetime.framework.idle.YieldStrategy;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;

final class RunnableConsumerStage extends AbstractRunnableStage {

	private final IdleStrategy idleStrategy;

	/**
	 * Creates a new instance with the {@link YieldStrategy} as default idle strategy.
	 *
	 * @param stage
	 *            to execute within an own thread
	 */
	public RunnableConsumerStage(final Stage stage) {
		this(stage, new YieldStrategy());
	}

	public RunnableConsumerStage(final Stage stage, final IdleStrategy idleStrategy) {
		super(stage);
		this.idleStrategy = idleStrategy;
	}

	@Override
	protected void beforeStageExecution() {
		logger.trace("ENTRY beforeStageExecution");

		do {
			checkforSignals();
			Thread.yield();
		} while (!stage.isStarted());

		logger.trace("EXIT beforeStageExecution");
	}

	@Override
	protected void executeStage() {
		try {
			this.stage.executeWithPorts();
		} catch (NotEnoughInputException e) {
			checkforSignals(); // check for termination
			executeIdleStrategy();
		}
	}

	private void executeIdleStrategy() {
		if (stage.shouldBeTerminated()) {
			return;
		}
		try {
			idleStrategy.execute();
		} catch (InterruptedException e) {
			// checkforSignals(); // check for termination
		}
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private void checkforSignals() {
		// FIXME should getInputPorts() really be defined in Stage?
		InputPort<?>[] inputPorts = stage.getInputPorts();
		logger.debug("Checking signals for: " + Arrays.toString(inputPorts));
		for (InputPort<?> inputPort : inputPorts) {
			IPipe pipe = inputPort.getPipe();
			if (pipe instanceof AbstractInterThreadPipe) { // TODO: is this needed?
				AbstractInterThreadPipe intraThreadPipe = (AbstractInterThreadPipe) pipe;
				ISignal signal = intraThreadPipe.getSignal();
				if (null != signal) {
					stage.onSignal(signal, inputPort);
				}
			}
		}
	}

	@Override
	protected void afterStageExecution() {
		// do nothing
	}

}
