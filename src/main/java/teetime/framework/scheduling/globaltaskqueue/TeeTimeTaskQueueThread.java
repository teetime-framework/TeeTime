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
package teetime.framework.scheduling.globaltaskqueue;

import org.jctools.queues.MpmcArrayQueue;

import teetime.framework.*;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

/**
 * Created by nilsziermann on 29.12.16.
 */

public class TeeTimeTaskQueueThread extends Thread {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private final GlobalTaskQueueScheduling scheduling;

	public TeeTimeTaskQueueThread(final GlobalTaskQueueScheduling scheduling) {
		this.scheduling = scheduling;
	}

	@Override
	public void run() {
		final MpmcArrayQueue<AbstractStage> taskQueue = scheduling.getTaskQueue();

		while (true) {
			AbstractStage stage = taskQueue.poll();
			if (stage != null) {
				executeStage(stage);
			} else {
				boolean finiteProducerStagesRunning = taskQueue.addAll(scheduling.getFiniteProducerStages());

				if (finiteProducerStagesRunning) {
					taskQueue.addAll(scheduling.getInfiniteProducerStages());
				} else {
					break;
				}
			}
		}
	}

	private void executeStage(AbstractStage stage) {
		final AbstractStage baseStage = stage;

		if (stage instanceof ITaskQueueDuplicable) {
			// TODO: Implement object pool for this.
			stage = ((ITaskQueueDuplicable) stage).duplicate();
		}
		// TODO: Handle TerminateException, NotEnoughInputException
		synchronized (stage) {
			if (isRunningStatefulStage(stage, baseStage)) {
				scheduling.getTaskQueue().add(stage);
			} else if (baseStage.getCurrentState() != StageState.TERMINATED && !STAGE_FACADE.shouldBeTerminated(baseStage)) {
				int numOfExecutions = 1;
				StageBuffer stageBuffer = new StageBuffer(stage, false);
				if (stage instanceof ITaskQueueDuplicable) {
					numOfExecutions = drainAndReserve(baseStage, stageBuffer);
				} else if (stage instanceof ITaskQueueInformation) {
					numOfExecutions = getMaxNumberOfExecutions(baseStage);
				} else {
					// TODO: Move to function
					scheduling.getRunningStatefulStages().put(stage, true);
				}
				// try {
				STAGE_FACADE.runStage(baseStage, numOfExecutions);
				// } catch (RuntimeException e) {
				// System.out.println("Stage with class: " + stage.getClass());
				// throw e;
				// }
				if (stage instanceof ITaskQueueDuplicable) {
					putInCorrectPipes(baseStage, stageBuffer);
				} else {
					// TODO: Move to function
					scheduling.getRunningStatefulStages().put(stage, false);
				}
			}
		}

		synchronized (baseStage) {
			// if (baseStage.getCurrentState() != StageState.TERMINATED) {
			// if (baseStage instanceof AbstractConsumerStage && baseStage.getCurrentState() != StageState.TERMINATING) {
			// checkForTerminationSignal(((AbstractConsumerStage<?>) baseStage));
			// }
			if (STAGE_FACADE.shouldBeTerminated(baseStage)) {
				afterStageExecution(baseStage);
			}
			// }
		}
	}

	private boolean isRunningStatefulStage(final AbstractStage stage, final AbstractStage baseStage) {
		return !(baseStage instanceof ITaskQueueDuplicable)
				&& scheduling.getRunningStatefulStages().containsKey(stage)
				&& scheduling.getRunningStatefulStages().get(stage);
	}

	// TODO: Move to stages?
	private int drainAndReserve(final AbstractStage baseStage, final StageBuffer stageBuffer) {
		synchronized (baseStage) {
			int numberOfExecutions = getMaxNumberOfExecutions(baseStage);
			replaceAndDrainToPipes(baseStage, stageBuffer, numberOfExecutions);
			scheduling.getStageList().get(baseStage).add(stageBuffer);
			return numberOfExecutions;
		}
	}

	// TODO: Move to stages?
	private void replaceAndDrainToPipes(final AbstractStage baseStage, final StageBuffer stageBuffer, final int numExecutions) {
		AbstractStage stage = stageBuffer.getStage();
		synchronized (baseStage) {
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				IPipe<?> oldPipe = inputPort.getPipe();
				IPipe<?> newPipe = new TaskQueueBufferPipe(inputPort, null, oldPipe);
				int numElementsToDrain = numExecutions * ((ITaskQueueInformation) stage).numElementsToDrainPerExecute(inputPort);
				for (int i = 0; i < numElementsToDrain; i++) {
					Object object = oldPipe.removeLast();
					newPipe.add(object);
				}
			}
		}
		for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
			IPipe<?> oldPipe = outputPort.getPipe();
			IPipe<?> newPipe = new TaskQueueBufferPipe(null, outputPort, oldPipe);
		}
		scheduling.getStageList().get(baseStage).add(stageBuffer);
	}

	// TODO: Move to stages?
	private int getMaxNumberOfExecutions(final AbstractStage stage) {
		synchronized (stage) {
			// TODO: Extract this value into Configuration or ConfigurationContext
			int maxNumExecutions = 1000;
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				int numExecutionsPort = inputPort.getPipe().size() / ((ITaskQueueInformation) stage).numElementsToDrainPerExecute(inputPort);
				maxNumExecutions = Math.min(numExecutionsPort, maxNumExecutions);
			}

			return maxNumExecutions;
		}
	}

	// TODO: Move to stages?
	private void putInCorrectPipes(final AbstractStage baseStage, final StageBuffer stageBuffer) {
		synchronized (baseStage) {
			// Mark element as done so it can be added to the list later
			stageBuffer.setDone(true);
			while (!scheduling.getStageList().get(baseStage).isEmpty() && scheduling.getStageList().get(baseStage).get(0).isDone()) {
				AbstractStage stage = scheduling.getStageList().get(baseStage).remove(0).getStage();
				for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
					IPipe<?> pipe = outputPort.getPipe();
					IPipe<?> replacedPipe = ((TaskQueueBufferPipe<?>) outputPort.getPipe()).getReplacedPipe();
					int n = pipe.size();
					for (int i = 0; i < n; i++) {
						Object object = pipe.removeLast();
						replacedPipe.add(object);
					}

				}
			}
		}
	}

	// private void checkForTerminationSignal(final AbstractConsumerStage baseStage) {
	// synchronized (baseStage) {
	// if (scheduling.getStageList().get(baseStage).size() != 0) {
	// return;
	// }
	// for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(baseStage)) {
	// if (!inputPort.isClosed()) {
	// return;
	// }
	// }
	//
	// baseStage.terminateStage();
	// }
	// }

	// TODO: Move to stages
	private void afterStageExecution(final AbstractStage baseStage) {
		synchronized (baseStage) {
			if (baseStage instanceof AbstractProducerStage) {
				baseStage.onSignal(new TerminatingSignal(), null);
				scheduling.getFiniteProducerStages().remove(baseStage);
			} else if (baseStage instanceof AbstractConsumerStage) {
				final ISignal signal = new TerminatingSignal(); // NOPMD DU caused by loop
				for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(baseStage)) {
					baseStage.onSignal(signal, inputPort);
				}
			}
		}
	}
}
