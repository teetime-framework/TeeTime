/**
 * Copyright © 2017 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.framework.scheduling.globaltaskqueue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import teetime.framework.*;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

public class TeeTimeTaskQueueThreadChw extends Thread {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private final GlobalTaskQueueScheduling scheduling;
	private final int numOfExecutions;

	public TeeTimeTaskQueueThreadChw(final GlobalTaskQueueScheduling scheduling, final int numOfExecutions) {
		super();
		this.scheduling = scheduling;
		this.numOfExecutions = numOfExecutions;
	}

	@Override
	public void run() {
		final AtomicInteger numNonTerminatedFiniteStages = scheduling.getNumNonTerminatedFiniteStages();
		final PrioritizedTaskPool taskPool = scheduling.getPrioritizedTaskPool(); // NOPMD (DU anomaly)

		while (numNonTerminatedFiniteStages.get() > 0) {
			AbstractStage stage = taskPool.removeNextStage();
			if (stage != null) {
				try {
					executeStage(stage);
					refillTaskPool(stage, taskPool);
				} finally {
					taskPool.releaseStage(stage); // release lock (FIXME bad API)
				}
			}
		}
	}

	private void executeStage(final AbstractStage stage) {
		STAGE_FACADE.runStage(stage, numOfExecutions);

		if (STAGE_FACADE.shouldBeTerminated(stage)) {
			afterStageExecution(stage);
		}

		if (stage.getCurrentState() == StageState.TERMINATED) {
			// int newValue =
			scheduling.getNumNonTerminatedFiniteStages().decrementAndGet();
			passFrontStatusToSuccessorStages(stage);
		}
	}

	private void afterStageExecution(final AbstractStage stage) {
		if (stage instanceof AbstractProducerStage) {
			stage.onSignal(new TerminatingSignal(), null);
		} else if (stage instanceof AbstractConsumerStage) {
			final ISignal signal = new TerminatingSignal(); // NOPMD DU caused by loop
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				stage.onSignal(signal, inputPort);
			}
		}
	}

	private void passFrontStatusToSuccessorStages(final AbstractStage stage) {
		List<AbstractStage> frontStages = scheduling.getFrontStages();
		synchronized (frontStages) {
			if (frontStages.contains(stage)) {
				frontStages.remove(stage);
				for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
					AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
					if (!STAGE_FACADE.shouldBeTerminated(targetStage) && targetStage.getCurrentState() != StageState.TERMINATED) {
						frontStages.add(targetStage);
					}
				}
			}
		}
	}

	private void refillTaskPool(final AbstractStage stage, final PrioritizedTaskPool taskPool) {
		// Add all successor stages so that they will be executed afterwards.
		// TODO evaluate whether adding workless stages is faster than adding stages within pipes.
		List<OutputPort<?>> outputPorts = STAGE_FACADE.getOutputPorts(stage);
		for (OutputPort<?> outputPort : outputPorts) {
			AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
			if (!STAGE_FACADE.shouldBeTerminated(targetStage) && targetStage.getCurrentState() != StageState.TERMINATED) {
				taskPool.scheduleStage(targetStage);
			}
		}

		// re-add stage to task queue if it is a front stage (a terminated front stage would have been already removed at this point)
		if (scheduling.getFrontStages().contains(stage)) {
			taskPool.scheduleStage(stage);
		}
	}
}
