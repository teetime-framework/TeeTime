/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package teetime.framework.concurrent;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.core.IPipeline;
import teetime.framework.core.IStage;
import teetime.util.StopWatch;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class WorkerThread extends Thread {

	private final IPipeline pipeline;
	private IStageScheduler stageScheduler;

	private volatile StageTerminationPolicy terminationPolicy;
	private volatile boolean shouldTerminate = false;
	private final int accessesDeviceId;
	private int executedUnsuccessfullyCount;

	// statistics
	private final StopWatch stopWatch = new StopWatch();
	private final List<Long> durationPer10000IterationsInNs = new LinkedList<Long>();
	private int iterations;

	public WorkerThread(final IPipeline pipeline, final int accessesDeviceId) {
		this.pipeline = pipeline;
		for (final IStage stage : pipeline.getStages()) {
			stage.setOwningThread(this);
		}
		this.accessesDeviceId = accessesDeviceId;
	}

	@Override
	public void run() {
		try {
			this.initDatastructures();
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}

		this.iterations = 0;
		this.stopWatch.start();

		while (this.stageScheduler.isAnyStageActive()) {
			this.iterations++;
			// this.iterationStopWatch.start();

			// beforeStageExecutionStopWatch.start();

			final IStage stage = this.stageScheduler.get();

			// beforeStageExecutionStopWatch.end();

			this.startStageExecution(stage);
			// stageExecutionStopWatch.start(); // expensive: takes 1/3 of overall time
			final boolean executedSuccessfully = stage.execute();
			// stageExecutionStopWatch.end();
			this.finishStageExecution(stage, executedSuccessfully);

			// afterStageExecutionStopWatch.start();

			if (this.shouldTerminate) {
				this.executeTerminationPolicy(stage, executedSuccessfully);
			}
			this.stageScheduler.determineNextStage(stage, executedSuccessfully);

			// afterStageExecutionStopWatch.end();

			// this.iterationStopWatch.end();

			// all stop watches are activated
			// final long schedulingOverhead = this.iterationStopWatch.getDurationInNs() -
			// stageExecutionStopWatch.getDurationInNs(); //4952

			// 6268 -> 5350 (w/o after) -> 4450 (w/o before) -> 3800 (w/o stage)
//			final long schedulingOverhead = this.iterationStopWatch.getDurationInNs();
			// final long schedulingOverhead = beforeStageExecutionStopWatch.getDurationInNs(); //327
			// final long schedulingOverhead = stageExecutionStopWatch.getDurationInNs(); //1416
			// final long schedulingOverhead = afterStageExecutionStopWatch.getDurationInNs(); //2450
			// rest: ~2000 (measurement overhead?)
			if ((iterations % 10000) == 0) {
				this.stopWatch.end();
				this.durationPer10000IterationsInNs.add(stopWatch.getDurationInNs());
				this.stopWatch.start();
			}
		}

		this.stopWatch.end();
		this.durationPer10000IterationsInNs.add(stopWatch.getDurationInNs());

		this.cleanUpDatastructures();
	}

	private void executeTerminationPolicy(final IStage executedStage, final boolean executedSuccessfully) {
		// System.out.println("WorkerThread.executeTerminationPolicy(): " + this.terminationPolicy +
		// ", executedSuccessfully=" + executedSuccessfully
		// + ", mayBeDisabled=" + executedStage.mayBeDisabled());

		switch (this.terminationPolicy) {
		case TERMINATE_STAGE_AFTER_NEXT_EXECUTION:
			if (executedStage.mayBeDisabled()) {
				this.stageScheduler.disable(executedStage);
			}
			break;
		case TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION:
			if (!executedSuccessfully) {
				if (executedStage.mayBeDisabled()) {
					this.stageScheduler.disable(executedStage);
				}
			}
			break;
		case TERMINATE_STAGE_NOW:
			for (final IStage stage : this.pipeline.getStages()) {
				this.stageScheduler.disable(stage);
			}
			break;
		default:
			break;
		}
	}

	private void initDatastructures() throws Exception {
		this.pipeline.fireStartNotification();
		this.stageScheduler = new NextStageScheduler(this.pipeline, this.accessesDeviceId);
	}

	private void startStageExecution(final IStage stage) {
		// System.out.println("Executing stage: " + stage);
	}

	private void finishStageExecution(final IStage stage, final boolean executedSuccessfully) {
		// System.out.println("Executed stage " + stage + " successfully: " + executedSuccessfully);
		if (!executedSuccessfully) { // statistics
			this.executedUnsuccessfullyCount++;
		}
	}

	private void cleanUpDatastructures() {
		// System.out.println("Cleaning up datastructures...");
		// System.out.println("Firing stop notification...");
		this.pipeline.fireStopNotification();
		// System.out.println("Thread terminated:" + this);
		// System.out.println(this.getName() + ": executedUnsuccessfullyCount=" + this.executedUnsuccessfullyCount);
	}

	public IPipeline getPipeline() {
		return this.pipeline;
	}

	// BETTER remove this method since it is not intuitive; add a check to onStartPipeline so that a stage automatically
	// disables itself if it has no input ports
	public void terminate(final StageTerminationPolicy terminationPolicyToUse) {
		for (final IStage startStage : this.pipeline.getStartStages()) {
			startStage.fireSignalClosingToAllInputPorts();
		}

		this.setTerminationPolicy(terminationPolicyToUse);
	}

	/**
	 * If not set, this thread will run infinitely.
	 *
	 * @param terminationPolicyToUse
	 */
	public void setTerminationPolicy(final StageTerminationPolicy terminationPolicyToUse) {
		this.terminationPolicy = terminationPolicyToUse;
		this.shouldTerminate = true;
	}

	public int getExecutedUnsuccessfullyCount() {
		return this.executedUnsuccessfullyCount;
	}

	public List<Long> getDurationPer10000IterationsInNs() {
		return durationPer10000IterationsInNs;
	}

	/**
	 * @since 1.10
	 */
	public int getIterations() {
		return iterations;
	}

}
