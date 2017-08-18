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
package teetime.framework.scheduling.globaltaskpool;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

class TeeTimeTaskQueueThreadChw extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeeTimeTaskQueueThreadChw.class);
	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private final GlobalTaskPoolScheduling scheduling;
	private final int numOfExecutions;
	private final CountDownLatch startPermission = new CountDownLatch(1);
	private final Semaphore runtimePermission = new Semaphore(0);

	private AbstractStage lastStage;

	public TeeTimeTaskQueueThreadChw(final GlobalTaskPoolScheduling scheduling, final int numOfExecutions) {
		super();
		this.scheduling = scheduling;
		this.numOfExecutions = numOfExecutions;
	}

	@Override
	public void run() {
		final CountDownAndUpLatch numNonTerminatedFiniteStages = scheduling.getNumRunningStages();
		final PrioritizedTaskPool taskPool = scheduling.getPrioritizedTaskPool(); // NOPMD (DU anomaly)
		final AbstractStage dummyStage = new AbstractStage() {
			@Override
			protected void execute() throws Exception {
				throw new UnsupportedOperationException("This stage implements the null object pattern");
			}
		};

		await();

		// TODO start processing not until receiving a sign by the scheduler #350

		LOGGER.debug("Started thread, running stages: {}", numNonTerminatedFiniteStages.getCurrentCount());

		while (numNonTerminatedFiniteStages.getCurrentCount() > 0) {
			processNextStage(taskPool, dummyStage);
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

	public void processNextStage(final PrioritizedTaskPool taskPool, final AbstractStage currentStage) {
		// taskPool.releaseStage(currentStage);

		AbstractStage stage = taskPool.removeNextStage();
		if (stage != null) {
			if (!scheduling.setIsBeingExecuted(stage, true)) { // TODO perhaps realize by compareAndSet(owningThread)
				// re-add stage
				while (!taskPool.scheduleStage(stage)) {
					throw new IllegalStateException(String.format("(processNextStage) Re-scheduling failed for paused stage %s", stage));
				}
				return;
			}

			if (lastStage != stage) {
				LOGGER.debug("Changed execution from {} to {}", lastStage, stage);
				lastStage = stage;
			}

			try {
				if (scheduling.isPausedStage(stage)) {
					// if (!scheduling.setIsBeingExecuted(stage, true)) { // TODO perhaps realize by compareAndSet(owningThread)
					// taskPool.scheduleStage(stage); // re-add stage
					// } else {
					scheduling.continueStage(stage);
					// }
				} /*
					 * else if (scheduling.isBeingExecuted(stage)) {
					 * taskPool.scheduleStage(stage); // re-add stage
					 * }
					 */else {
					// if (!scheduling.setIsBeingExecuted(stage, true)) { // TODO perhaps realize by compareAndSet(owningThread)
					// taskPool.scheduleStage(stage); // re-add stage
					// } else {
					// try {
					executeStage(stage);
					refillTaskPool(stage, taskPool);
					// } finally {
					// taskPool.releaseStage(stage); // release lock (FIXME bad API)
					// }
					// }
				}
			} finally {
				scheduling.setIsBeingExecuted(stage, false);
			}
		}
	}

	private void executeStage(final AbstractStage stage) {
		scheduling.setOwningThreadSynced(stage, this);

		LOGGER.debug("Executing {}", stage);
		STAGE_FACADE.runStage(stage, numOfExecutions);

		// FIXME is executed several times whenever <unknown so far>
		if (STAGE_FACADE.shouldBeTerminated(stage)) {
			// if (stages.containsKey(stage)) {
			// throw new IllegalStateException(String.format("Already terminating %s", stage));
			// }
			// stages.put(stage, Boolean.TRUE);

			passFrontStatusToSuccessorStages(stage);

			afterStageExecution(stage);
			if (stage.getCurrentState() != StageState.TERMINATED) {
				throw new IllegalStateException(
						String.format("(TeeTimeTaskQueueThreadChw) %s: Expected state TERMINATED, but was %s", stage, stage.getCurrentState()));
			}
			scheduling.getNumRunningStages().countDown();
		}
	}

	private void afterStageExecution(final AbstractStage stage) {
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
			if (frontStages.contains(stage)) {
				frontStages.remove(stage);
				PrioritizedTaskPool taskPool = scheduling.getPrioritizedTaskPool();
				for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
					AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
					if (targetStage.getCurrentState().compareTo(StageState.TERMINATING) < 0) {
						frontStages.add(targetStage);
						while (!taskPool.scheduleStage(targetStage)) {
							throw new IllegalStateException(String.format("(passFrontStatusToSuccessorStages) Scheduling successor failed for %s", targetStage));
						}
					}
				}
				LOGGER.debug("New front stages {}\n{}", frontStages, taskPool);
			}
		}
	}

	private void refillTaskPool(final AbstractStage stage, final PrioritizedTaskPool taskPool) {
		Set<AbstractStage> frontStages = scheduling.getFrontStages();
		// re-add stage to task queue if it is a front stage (a terminated front stage would have been already removed at this point)
		if (frontStages.contains(stage)) {
			while (!taskPool.scheduleStage(stage)) {
				throw new IllegalStateException(String.format("(refillTaskPool) Self-scheduling failed for front stage %s", stage));
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
			throw new IllegalStateException(String.format("Expected this thread, but was %s", Thread.currentThread()));
		}
		await();
	}
}
