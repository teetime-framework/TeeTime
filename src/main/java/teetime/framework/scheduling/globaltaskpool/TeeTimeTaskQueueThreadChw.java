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

import java.util.List;
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

		LOGGER.debug("Started processing: {}", numNonTerminatedFiniteStages.getCurrentCount());

		while (numNonTerminatedFiniteStages.getCurrentCount() > 0) {
			processNextStage(taskPool, dummyStage);
		}

		LOGGER.debug("Terminated thread: {}, running stages: {}", this, numNonTerminatedFiniteStages.getCurrentCount());
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
			try {
				if (scheduling.isPausedStage(stage)) {
					if (!scheduling.setIsBeingExecuted(stage, true)) { // TODO perhaps realize by compareAndSet(owningThread)
						taskPool.scheduleStage(stage); // re-add stage
					} else {
						stage.setPaused(false);
						scheduling.continueStage(stage);
					}
				} else if (scheduling.isBeingExecuted(stage)) {
					taskPool.scheduleStage(stage); // re-add stage
				} else {
					if (!scheduling.setIsBeingExecuted(stage, true)) { // TODO perhaps realize by compareAndSet(owningThread)
						taskPool.scheduleStage(stage); // re-add stage
					} else {
						// try {
						executeStage(stage);
						refillTaskPool(stage, taskPool);
						// } finally {
						// taskPool.releaseStage(stage); // release lock (FIXME bad API)
						// }
					}
				}
			} finally {
				scheduling.setIsBeingExecuted(stage, false);
			}
		}
	}

	private void executeStage(final AbstractStage stage) {
		STAGE_FACADE.setOwningThread(stage, this);

		LOGGER.debug("Executing {}", stage);
		STAGE_FACADE.runStage(stage, numOfExecutions);

		// FIXME is executed several times whenever <unknown so far>
		if (STAGE_FACADE.shouldBeTerminated(stage)) {
			// if (stages.containsKey(stage)) {
			// throw new IllegalStateException(String.format("Already terminating %s", stage));
			// }
			// stages.put(stage, Boolean.TRUE);
			afterStageExecution(stage);
			if (stage.getCurrentState() != StageState.TERMINATED) {
				throw new IllegalStateException(String.format("%s: Expected state TERMINATED, but was %s", stage, stage.getCurrentState()));
			}
			scheduling.getNumRunningStages().countDown();
			passFrontStatusToSuccessorStages(stage);
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
		List<AbstractStage> frontStages = scheduling.getFrontStages();
		synchronized (frontStages) {
			if (frontStages.contains(stage)) {
				frontStages.remove(stage);
				for (OutputPort<?> outputPort : STAGE_FACADE.getOutputPorts(stage)) {
					AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();
					if (!STAGE_FACADE.shouldBeTerminated(targetStage) && targetStage.getCurrentState() != StageState.TERMINATED) {
						frontStages.add(targetStage);
						scheduling.getPrioritizedTaskPool().scheduleStage(targetStage);
					}
				}
				LOGGER.debug("New front stages {}", frontStages);
			}
		}
	}

	private void refillTaskPool(final AbstractStage stage, final PrioritizedTaskPool taskPool) {
		// re-add stage to task queue if it is a front stage (a terminated front stage would have been already removed at this point)
		if (scheduling.getFrontStages().contains(stage)) {
			taskPool.scheduleStage(stage);
		}
	}

	/**
	 * Should be executed by a different thread.
	 */
	public void awake() {
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
