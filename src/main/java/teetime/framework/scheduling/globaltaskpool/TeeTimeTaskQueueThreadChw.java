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
package teetime.framework.scheduling.globaltaskpool;

import java.util.Set;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.StageFacade;
import teetime.framework.StageState;
import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.scheduling.CountDownAndUpLatch;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

class TeeTimeTaskQueueThreadChw extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeeTimeTaskQueueThreadChw.class);
	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private final GlobalTaskPoolScheduling scheduling;
	private final int numOfExecutions;
	private final Semaphore runtimePermission = new Semaphore(0);

	private AbstractStage lastStage;
	private AbstractExceptionListener listener;

	public TeeTimeTaskQueueThreadChw(final GlobalTaskPoolScheduling scheduling, final int numOfExecutions) {
		super();
		this.scheduling = scheduling;
		this.numOfExecutions = numOfExecutions;
	}

	@Override
	public void run() {
		final CountDownAndUpLatch numNonTerminatedFiniteStages = scheduling.getNumRunningStages();
		final PrioritizedTaskPool taskPool = scheduling.getPrioritizedTaskPool(); // NOPMD (DU anomaly)

		await();

		// TODO start processing not until receiving a sign by the scheduler #350

		LOGGER.debug("Started thread, running stages: {}", numNonTerminatedFiniteStages.getCurrentCount());

		while (numNonTerminatedFiniteStages.getCurrentCount() > 0) {
			processNextStage(taskPool);
		}

		LOGGER.debug("Terminated thread, running stages: {}", numNonTerminatedFiniteStages.getCurrentCount());
	}

	private void await() {
		try {
			runtimePermission.acquire();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void processNextStage(final PrioritizedTaskPool taskPool) {
		AbstractStage stage = taskPool.removeNextStage();
		if (stage == null) { // no stage available in the pool
			return;
		}

		// what's the purpose of this flag?:
		// ensures that only one thread executes the stage instance at once
		// TODO perhaps realize with the owningThread variable and remove the additional beingExecuted variable
		if (!scheduling.setIsBeingExecuted(stage, true)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} is being executed. Trying another stage...", stage);
			}

			// process next stage (potentially across the current level index)
			// without re-adding the current stage in order to prevent re-executing this stage again
			processNextStage(taskPool); // recursive call

			// re-add stage
			if (!taskPool.scheduleStage(stage)) {
				throw new IllegalStateException(String.format("(processNextStage) Re-scheduling failed for paused %s", stage));
			}
			return;
		}

		if (lastStage != stage) { // for debugging purposes only
			LOGGER.trace("Changed execution from {} to {}", lastStage, stage);
			lastStage = stage;
		}

		if (scheduling.isPausedStage(stage)) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Stage is paused. Continueing stage: {}", stage);
			}
			scheduling.continueStage(stage);
		} else {
			try {
				// do nothing if the stage is about to terminate or has already been terminated
				if (stage.getCurrentState().isAfter(StageState.STARTED)) {
					LOGGER.trace("Skipped execution since the stage is terminating: {}", stage);
					// throw new IllegalStateException();
					return;
				}

				Thread owningThread = scheduling.getOwningThreadSynched(stage);
				if (null != owningThread) {
					String message = String.format("%s vs. %s", owningThread, Thread.currentThread());
					throw new IllegalStateException(message);
				}
				scheduling.setOwningThreadSynced(stage, this);

				try {
					executeStage(stage);

					reschedule(stage);
				} finally {
					scheduling.setOwningThreadSynced(stage, null);
				}
			} finally {
				scheduling.setIsBeingExecuted(stage, false);
			}
		}

	}

	private void executeStage(final AbstractStage stage) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executing {}", stage);
		}

		STAGE_FACADE.setExceptionHandler(stage, listener); // FIXME do not set it on each execution
		STAGE_FACADE.runStage(stage, numOfExecutions);

		if (STAGE_FACADE.shouldBeTerminated(stage)) {
			// LOGGER.info("TERMINATING {}", stage);

			sendTerminationSignal(stage);

			if (stage.getCurrentState() != StageState.TERMINATED) {
				String message = String.format("(TeeTimeTaskQueueThreadChw) %s: Expected state TERMINATED, but was %s", stage, stage.getCurrentState());
				throw new IllegalStateException(message);
			}

			// since afterStageExecution() can still send elements,
			// passFrontStatusToSuccessorStages(stage) must be behind
			passFrontStatusToSuccessorStages(stage);

			scheduleSuccessorStages(stage);

			scheduling.getNumRunningStages().countDown();
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Executed {}", stage);
		}
	}

	private void sendTerminationSignal(final AbstractStage stage) {
		if (stage.isProducer()) {
			stage.onSignal(new TerminatingSignal(), null);
		} else { // is consumer
			final ISignal signal = new TerminatingSignal(); // NOPMD DU caused by loop
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				stage.onSignal(signal, inputPort);
			}
		}
	}

	private void passFrontStatusToSuccessorStages(final AbstractStage stage) {
		// a set, not a list since multiple predecessors of a merger would add the merger multiple times
		Set<AbstractStage> frontStages = scheduling.getFrontStages();
		synchronized (frontStages) {
			frontStages.remove(stage);

			for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
				AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
				if (targetStage.getCurrentState().isBefore(StageState.TERMINATING)) {
					frontStages.add(targetStage);
				}
			}
			LOGGER.info("New front stages {}", frontStages);
		}
	}

	private void scheduleSuccessorStages(final AbstractStage stage) {
		ScheduleQueue taskPool = scheduling.getPrioritizedTaskPool();
		for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
			AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
			if (targetStage.getCurrentState().isBefore(StageState.TERMINATING)) {

				if (!taskPool.scheduleStage(targetStage)) {
					String message = String.format("(passFrontStatusToSuccessorStages) Scheduling successor failed for %s", targetStage);
					throw new IllegalStateException(message);
				}
			}
		}
	}

	private void reschedule(final AbstractStage stage) {
		if (!STAGE_FACADE.shouldBeTerminated(stage)) {
			boolean reschedule = stage.isProducer();

			// Only schedule this (consumer) stage if it has input to consume.
			// Otherwise, wait for a predecessor stage to re-schedule this stage again.
			for (InputPort<?> inputPort : STAGE_FACADE.getInputPorts(stage)) {
				if (inputPort.getPipe().hasMore()) {
					reschedule = true;
					break;
				}
			}

			ScheduleQueue taskPool = scheduling.getPrioritizedTaskPool();
			if (reschedule && !taskPool.scheduleStage(stage)) {
				String message = String.format("(reschedule) Scheduling stage again failed for %s", stage);
				throw new IllegalStateException(message);
			}
		}
	}

	/**
	 * Should be executed by a different thread.
	 */
	public void awake() {
		LOGGER.debug("Awaking {}", this);
		runtimePermission.release();
	}

	/**
	 * Must be executed by the current thread.
	 */
	public void pause() {
		if (Thread.currentThread() != this) {
			String message = String.format("Expected this thread, but was %s", Thread.currentThread());
			throw new IllegalStateException(message);
		}
		await();
	}

	public void setExceptionListener(final AbstractExceptionListener listener) {
		this.listener = listener;
	}
}
