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

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.basic.distributor.dynamic.CreatePortActionDistributor;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.distributor.dynamic.RemovePortActionDistributor;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.taskfarm.ITaskFarmDuplicable;
import teetime.stage.taskfarm.TaskFarmStage;
import teetime.stage.taskfarm.exception.TaskFarmControllerException;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;
import teetime.util.framework.port.PortAction;

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
 *            Type of enclosed Stage (must extend {@link ITaskFarmDuplicable})
 */
public class TaskFarmController<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private final TaskFarmStage<I, O, T> taskFarmStage;

	/**
	 * Constructor.
	 *
	 * @param configuration
	 *            TaskFarmConfiguration of the Task Farm which
	 *            this controller is used for
	 */
	TaskFarmController(final TaskFarmStage<I, O, T> taskFarmStage) {
		this.taskFarmStage = taskFarmStage;
	}

	/**
	 * Dynamically adds a stage to the controlled task farm.
	 */
	public void addStageToTaskFarm() {
		@SuppressWarnings("unchecked")
		final T newStage = (T) this.taskFarmStage.getBasicEnclosedStage().duplicate();

		final CreatePortActionMerger<O> mergerPortAction =
				new CreatePortActionMerger<O>(newStage.getOutputPort());
		this.taskFarmStage.getMerger().addPortActionRequest(mergerPortAction);
		mergerPortAction.waitForCompletion();

		final PortAction<DynamicDistributor<I>> distributorPortAction =
				new CreatePortActionDistributor<I>(newStage.getInputPort());
		this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);

		this.addNewEnclosedStageInstance(newStage);
	}

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 */
	public void removeStageFromTaskFarm() {
		final ITaskFarmDuplicable<I, O> stageToBeRemoved = this.getStageToBeRemoved();
		final OutputPort<?> distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			final PortAction<DynamicDistributor<I>> distributorPortAction =
					new RemovePortActionDistributor<I>((OutputPort<I>) distributorOutputPort);
			this.taskFarmStage.getDistributor().addPortActionRequest(distributorPortAction);
		} catch (ClassCastException e) {
			throw new TaskFarmControllerException("Merger and Distributor have a different type than the Task Farm or the Task Farm Controller.");
		}
	}

	private void addNewEnclosedStageInstance(final T newStage) {
		this.taskFarmStage.getEnclosedStageInstances().add(newStage);
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final OutputPort<?> distributorOutputPort = stageToBeRemoved.getInputPort().getPipe().getSourcePort();
		return distributorOutputPort;
	}

	private ITaskFarmDuplicable<I, O> getStageToBeRemoved() {
		List<ITaskFarmDuplicable<I, O>> stageInstances = this.taskFarmStage.getEnclosedStageInstances();
		return stageInstances.get(getStageIndexWithLeastRemainingInput());
	}

	private int getStageIndexWithLeastRemainingInput() {
		int currentMinimum = Integer.MAX_VALUE;
		int currentMinumumStageIndex = taskFarmStage.getEnclosedStageInstances().size() - 1;

		for (int i = 0; i < taskFarmStage.getEnclosedStageInstances().size(); i++) {
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

			if (monitorablePipe.size() < currentMinimum) {
				currentMinimum = monitorablePipe.size();
				currentMinumumStageIndex = i;
			}
		}

		return currentMinumumStageIndex;
	}
}
