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
package teetime.stage.taskfarm.adaptation.reconfiguration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.RuntimeServiceFacade;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.stage.basic.distributor.dynamic.CreatePortActionDistributor;
import teetime.stage.basic.distributor.dynamic.RemovePortActionDistributor;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmControllerException;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

/**
 * The TaskFarmController is able to dynamically add stages to and remove
 * stages from a Task Farm with the same type at runtime.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage (must extend {@link ITaskFarmDuplicable})
 */
class TaskFarmController<I, O> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskFarmController.class);

	private final TaskFarmStage<I, O, ?> taskFarmStage;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this controller is used for
	 */
	<T extends ITaskFarmDuplicable<I, O>> TaskFarmController(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	/**
	 * Dynamically adds a stage to the controlled task farm.
	 *
	 * @throws InterruptedException
	 */
	public void addStageToTaskFarm() throws InterruptedException {
		// FIXME limit number of stages to cores-2. consider to move setting to config
		LOGGER.debug("Add stage (current amount of stages: " + taskFarmStage.getEnclosedStageInstances().size() + ")");
		ITaskFarmDuplicable<I, O> newStage = this.taskFarmStage.getBasicEnclosedStage().duplicate();

		final CreatePortActionDistributor<I> distributorPortAction = new CreatePortActionDistributor<I>(newStage.getInputPort(),
				taskFarmStage.getConfiguration().getPipeCapacity());
		this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);
		LOGGER.debug("distributor port created, before wait");
		LOGGER.debug("state of distributor: " + this.taskFarmStage.getDistributor().getCurrentState().toString());
		try {
			distributorPortAction.waitForCompletion();
		} catch (InterruptedException e) {
			LOGGER.debug("Interrupted while waiting for completion", e);
			throw e;
		}
		LOGGER.debug("distributor port created");

		final CreatePortActionMerger<O> mergerPortAction = new CreatePortActionMerger<O>(newStage.getOutputPort(),
				taskFarmStage.getConfiguration().getPipeCapacity());
		this.taskFarmStage.getMerger().addPortActionRequest(mergerPortAction);
		mergerPortAction.waitForCompletion();
		LOGGER.debug("merger port created");

		RuntimeServiceFacade.INSTANCE.startWithinNewThread(taskFarmStage.getDistributor(), newStage.getInputPort().getOwningStage());
		LOGGER.debug("Started new thread");

		this.addNewEnclosedStageInstance(newStage);
		this.addNewPipeToMonitoring(newStage);
		LOGGER.debug("Finished: Add stage (current amount of stages: " + taskFarmStage.getEnclosedStageInstances().size() + ")");
	}

	private void addNewPipeToMonitoring(final ITaskFarmDuplicable<I, O> newStage) {
		if (this.taskFarmStage.getConfiguration().isMonitoringEnabled()) {
			try {
				this.taskFarmStage.getPipeMonitoringService().addMonitoredItem((IMonitorablePipe) newStage.getInputPort().getPipe());
			} catch (ClassCastException e) {
				throw new TaskFarmControllerException("A generated pipe is not monitorable.");
			}
		}
	}

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 *
	 * @throws InterruptedException
	 */
	public void removeStageFromTaskFarm() throws InterruptedException {
		if (taskFarmStage.getEnclosedStageInstances().size() == 1) {
			return;
		}

		LOGGER.debug("Remove stage (current amount of stages: " + taskFarmStage.getEnclosedStageInstances().size() + ")");

		ITaskFarmDuplicable<I, O> stageToBeRemoved = null;
		OutputPort<?> distributorOutputPort = null;

		stageToBeRemoved = this.getStageToBeRemoved();
		distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			final RemovePortActionDistributor<I> distributorPortAction = new RemovePortActionDistributor<I>((OutputPort<I>) distributorOutputPort);
			this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);
			this.taskFarmStage.getEnclosedStageInstances().remove(stageToBeRemoved);
			LOGGER.debug("WAIT for " + distributorPortAction);
			distributorPortAction.waitForCompletion();

			LOGGER.debug("Finished: Remove stage (current amount of stages: " + taskFarmStage.getEnclosedStageInstances().size() + ")");
		} catch (ClassCastException e) {
			throw new TaskFarmControllerException("Merger and Distributor have a different type than the Task Farm or the Task Farm Controller.");
		}
	}

	private void addNewEnclosedStageInstance(final ITaskFarmDuplicable<I, O> newStage) {
		this.taskFarmStage.getEnclosedStageInstances().add(newStage);
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final InputPort<?> inputPortOfStage = stageToBeRemoved.getInputPort();
		final IPipe<?> pipeInBetween = inputPortOfStage.getPipe();
		final OutputPort<?> distributorOutputPort = pipeInBetween.getSourcePort();
		return distributorOutputPort;
	}

	private ITaskFarmDuplicable<I, O> getStageToBeRemoved() {
		List<ITaskFarmDuplicable<I, O>> stageInstances = this.taskFarmStage.getEnclosedStageInstances();
		return stageInstances.get(getStageIndexWithLeastRemainingInput());
	}

	private int getStageIndexWithLeastRemainingInput() {
		int currentMinimum = Integer.MAX_VALUE;
		int currentMinumumStageIndex = taskFarmStage.getEnclosedStageInstances().size() - 1;

		// do not remove basic stage
		for (int i = 1; i < taskFarmStage.getEnclosedStageInstances().size(); i++) {
			ITaskFarmDuplicable<I, O> instance = taskFarmStage.getEnclosedStageInstances().get(i);
			InputPort<I> port = instance.getInputPort();
			IMonitorablePipe monitorablePipe = null;

			try {
				monitorablePipe = (IMonitorablePipe) port.getPipe();
			} catch (ClassCastException e) {
				throw new TaskFarmInvalidPipeException(
						"The input pipe of an enclosed stage instance inside a Task Farm"
								+ " does not implement IMonitorablePipe, which is required. Instead, the type is "
								+ port.getPipe().getClass().getSimpleName() + ".");
			}

			if (monitorablePipe != null && monitorablePipe.size() < currentMinimum) {
				currentMinimum = monitorablePipe.size();
				currentMinumumStageIndex = i;
			}
		}

		LOGGER.debug("Remove stage (currentMinumumStageIndex: " + currentMinumumStageIndex + ")");
		return currentMinumumStageIndex;
	}
}
