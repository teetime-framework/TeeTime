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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import teetime.framework.core.IInputPort;
import teetime.framework.core.IInputPort.PortState;
import teetime.framework.core.IPipe;
import teetime.framework.core.IPipeCommand;
import teetime.framework.core.IPipeline;
import teetime.framework.core.IStage;
import teetime.framework.scheduling.NextStageScheduler;
import teetime.framework.scheduling.StageStateManager;
import teetime.util.StopWatch;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class WorkerThread extends Thread {

	private static final int NUM_ITERATIONS_TO_MEASURE = 100000;

	private final IPipeline pipeline;
	private IStageScheduler stageScheduler;
	private StageStateManager stageStateManager;

	private volatile StageTerminationPolicy terminationPolicy;
	private volatile boolean shouldTerminate = false;
	private final int accessesDeviceId;
	private int executedUnsuccessfullyCount;

	// statistics
	private final StopWatch stopWatch = new StopWatch();
	private final List<Long> durationPerXIterationsInNs = new LinkedList<Long>();
	private int iterations;

	public WorkerThread(final IPipeline pipeline, final int accessesDeviceId) {
		this.pipeline = pipeline;
		this.accessesDeviceId = accessesDeviceId;
	}

	private void initStages(final IPipeline pipeline) {
		for (final IStage stage : pipeline.getStages()) {
			stage.setOwningThread(this);
		}

		this.setDepthForEachStage(pipeline);
		this.setSchedulingIndexForEachhStage(pipeline);
	}

	private void setDepthForEachStage(final IPipeline pipeline) {
		final IPipeCommand setDepthCommand = new IPipeCommand() {
			@Override
			public void execute(final IPipe<?> pipe) throws Exception {
				final IStage sourceStage = pipe.getSourcePort().getOwningStage();
				final IStage owningStage = pipe.getTargetPort().getOwningStage();
				if (owningStage.getDepth() == IStage.DEPTH_NOT_SET) {
					owningStage.setDepth(sourceStage.getDepth() + 1);
					owningStage.notifyOutputPipes(this);
				}
			}
		};

		for (final IStage startStage : pipeline.getStartStages()) {
			startStage.setDepth(0);
		}

		for (final IStage startStage : pipeline.getStartStages()) {
			try {
				startStage.notifyOutputPipes(setDepthCommand);
			} catch (final Exception e) {
				throw new IllegalStateException("may not happen", e);
			}
		}
	}

	private List<IStage> setSchedulingIndexForEachhStage(final IPipeline pipeline) {
		final List<IStage> stageList = new ArrayList<IStage>(pipeline.getStages());

		final Comparator<? super IStage> depthComparator = new Comparator<IStage>() {
			@Override
			public int compare(final IStage o1, final IStage o2) {
				if (o1.getDepth() == o2.getDepth()) {
					return 0;
				} else if (o1.getDepth() < o2.getDepth()) {
					return -1;
				} else {
					return 1;
				}
			}
		};

		Collections.sort(stageList, depthComparator);

		for (int i = 0; i < stageList.size(); i++) {
			stageList.get(i).setSchedulingIndex(i);
		}

		return stageList;
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
			// stageExecutionStopWatch.start();
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
			// final long schedulingOverhead = this.iterationStopWatch.getDurationInNs();
			// final long schedulingOverhead = beforeStageExecutionStopWatch.getDurationInNs(); //327
			// final long schedulingOverhead = stageExecutionStopWatch.getDurationInNs(); //1416
			// final long schedulingOverhead = afterStageExecutionStopWatch.getDurationInNs(); //2450
			// rest: ~2000 (measurement overhead?)
			if ((this.iterations % NUM_ITERATIONS_TO_MEASURE) == 0) {
				this.stopWatch.end();
				this.durationPerXIterationsInNs.add(this.stopWatch.getDurationInNs());
				this.stopWatch.start();
			}
		}

		this.stopWatch.end();
		this.durationPerXIterationsInNs.add(this.stopWatch.getDurationInNs());

		this.cleanUpDatastructures();
	}

	private void executeTerminationPolicy(final IStage executedStage, final boolean executedSuccessfully) {
		// System.out.println("executeTerminationPolicy executedStage=" + executedStage + ", executedSuccessfully=" + executedSuccessfully);
		// System.out.println("executeTerminationPolicy areAllInputPortsClosed(executedStage)=" + this.stageStateManager.areAllInputPortsClosed(executedStage));

		switch (this.terminationPolicy) {
		case TERMINATE_STAGE_AFTER_NEXT_EXECUTION:
			if (this.stageStateManager.areAllInputPortsClosed(executedStage)) {
				this.stageScheduler.disable(executedStage);
			}
			break;
		case TERMINATE_STAGE_AFTER_UNSUCCESSFUL_EXECUTION:
			if (!executedSuccessfully) {
				if (this.stageStateManager.areAllInputPortsClosed(executedStage)) {
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
		// stages need to be initialized here, because in a concurrent context some stages (e.g., a merger) is executed after its pipeline has been created.
		this.initStages(this.pipeline);
		this.stageStateManager = new StageStateManager(this.pipeline);
		this.stageScheduler = new NextStageScheduler(this.pipeline, this.accessesDeviceId, this.stageStateManager);

		for (final IStage startStage : this.pipeline.getStartStages()) {
			for (IInputPort<IStage, ?> inputPort : startStage.getInputPorts()) {
				if (inputPort.getState() == PortState.CLOSED) {
					inputPort.close();
				}
			}
		}

		this.pipeline.fireStartNotification();
	}

	private final void startStageExecution(final IStage stage) {
		// System.out.println("Executing stage: " + stage);
	}

	private final void finishStageExecution(final IStage stage, final boolean executedSuccessfully) {
		if (!executedSuccessfully) { // statistics
			this.executedUnsuccessfullyCount++;
		}
	}

	private void cleanUpDatastructures() {
		this.pipeline.fireStopNotification();
	}

	public IPipeline getPipeline() {
		return this.pipeline;
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
		return this.durationPerXIterationsInNs;
	}

	/**
	 * @since 1.10
	 */
	public int getIterations() {
		return this.iterations;
	}

}
