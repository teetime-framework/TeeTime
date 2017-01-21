/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage.taskfarm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.stage.basic.distributor.dynamic.*;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.basic.merger.strategy.SkippingBusyWaitingRoundRobinStrategy;
import teetime.stage.taskfarm.exception.TaskFarmControllerException;
import teetime.stage.taskfarm.exception.TaskFarmInvalidPipeException;

/**
 * Represents the self-adaptive task farm parallelization pattern in
 * TeeTime. It dynamically adds CPU resources at runtime depending on
 * the current CPU load and the behavior of the parallelized stage.
 *
 * @author Christian Claus Wiechmann, Christoph Dornieden
 *
 * @param <I>
 *            Input type of task Farm
 * @param <O>
 *            Output type of task Farm
 * @param <T>
 *            Type of the parallelized stage
 */
public class DynamicTaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends CompositeStage {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicTaskFarmStage.class);

	/** currently existing worker stages **/
	private final List<ITaskFarmDuplicable<I, O>> enclosedStageInstances = new ArrayList<ITaskFarmDuplicable<I, O>>();

	/** distributor instance **/
	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	/** merger instance **/
	private final DynamicMerger<O> merger;
	/** pipeCapacity **/
	private final int pipeCapacity;

	/**
	 * Create a task farm using a worker stage with a pipe capacity of 100.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 */
	public DynamicTaskFarmStage(final T workerStage) {
		this(workerStage, null, 100);
	}

	/**
	 * Create a task farm using a worker stage with a given pipe capacity.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 * @param pipeCapacity
	 *            pipe capacity to be used
	 */
	public DynamicTaskFarmStage(final T workerStage, final int pipeCapacity) {
		this(workerStage, null, pipeCapacity);
	}

	DynamicTaskFarmStage(final T workerStage, final DynamicMerger<O> merger, final int pipeCapacity) {
		super();
		this.pipeCapacity = pipeCapacity;
		if (null == workerStage) {
			throw new IllegalArgumentException("The constructor of a Task Farm may not be called with null as the worker stage.");
		}
		if (merger == null) {
			this.merger = new DynamicMerger<O>(new SkippingBusyWaitingRoundRobinStrategy());
		} else {
			this.merger = merger;
		}
		// TODO init with multiple worker stages (perhaps, extend StaticTaskFarmStage)
		this.init(workerStage);
	}

	private void init(final T includedStage) {
		// includedStage.setTaskFarmStage(this);

		final InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort, pipeCapacity);

		final OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort(), pipeCapacity);

		// this.merger.declareActive();
		// includedStage.getInputPort().getOwningStage().declareActive();

		this.enclosedStageInstances.add(includedStage);
	}

	/**
	 * Adds a new enclosed stage at Runtime
	 *
	 * @return new created Stage
	 * @throws InterruptedException
	 *
	 * @author Christian Claus Wiechmann, Christoph Dornieden (code moved from TaskFarmController)
	 */
	public ITaskFarmDuplicable<I, O> addStageAtRuntime() throws InterruptedException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Add stage (current amount of stages: {})", enclosedStageInstances.size());
		}

		if (!merger.isActive()) {
			merger.declareActive();
		}

		AbstractStage basicEnclosedStage = getBasicEnclosedStage().getInputPort().getOwningStage();
		if (!basicEnclosedStage.isActive()) {
			basicEnclosedStage.declareActive();
		}

		final ITaskFarmDuplicable<I, O> newStage = getBasicEnclosedStage().duplicate();
		// newStage.setTaskFarmStage(this);

		final CreatePortActionDistributor<I> distributorPortAction = new CreatePortActionDistributor<I>(newStage.getInputPort(),
				pipeCapacity);
		distributor.addPortActionRequest(distributorPortAction);

		distributorPortAction.waitForCompletion();

		final CreatePortActionMerger<O> mergerPortAction = new CreatePortActionMerger<O>(newStage.getOutputPort(),
				pipeCapacity);
		merger.addPortActionRequest(mergerPortAction);

		mergerPortAction.waitForCompletion();

		RuntimeServiceFacade.INSTANCE.startWithinNewThread(distributor, newStage.getInputPort().getOwningStage());

		enclosedStageInstances.add(newStage);
		return newStage;
	}

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 *
	 * @throws InterruptedException
	 *
	 * @author Christian Claus Wiechmann, Christoph Dornieden (code moved from TaskFarmController)
	 */
	public ITaskFarmDuplicable<I, O> removeStageAtRuntime() throws InterruptedException {
		if (enclosedStageInstances.size() == 1) {
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Remove stage (current amount of stages: {})", enclosedStageInstances.size());
		}

		ITaskFarmDuplicable<I, O> stageToBeRemoved = enclosedStageInstances.get(getStageIndexWithLeastRemainingInput());
		OutputPort<?> distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);

		try {
			@SuppressWarnings("unchecked")
			final RemovePortActionDistributor<I> distributorPortAction = new RemovePortActionDistributor<I>((OutputPort<I>) distributorOutputPort);
			distributor.addPortActionRequest(distributorPortAction);
			enclosedStageInstances.remove(stageToBeRemoved);

			distributorPortAction.waitForCompletion();

		} catch (ClassCastException e) {
			throw new TaskFarmControllerException("Merger and Distributor have a different type than the Task Farm or the Task Farm Controller.", e);
		}
		return stageToBeRemoved;
	}

	private int getStageIndexWithLeastRemainingInput() {
		int currentMinimum = Integer.MAX_VALUE;
		int currentMinumumStageIndex = enclosedStageInstances.size() - 1;

		// do not remove basic stage
		for (int i = 1; i < enclosedStageInstances.size(); i++) {
			final ITaskFarmDuplicable<I, O> instance = enclosedStageInstances.get(i);
			InputPort<I> port = instance.getInputPort();
			IMonitorablePipe monitorablePipe = null;

			try {
				monitorablePipe = (IMonitorablePipe) port.getPipe();
			} catch (ClassCastException e) {
				throw new TaskFarmInvalidPipeException(
						"The input pipe of an enclosed stage instance inside a Task Farm"
								+ " does not implement IMonitorablePipe, which is required. Instead, the type is "
								+ port.getPipe().getClass().getSimpleName() + ".",
						e);
			}

			if (monitorablePipe != null && monitorablePipe.size() < currentMinimum) {
				currentMinimum = monitorablePipe.size();
				currentMinumumStageIndex = i;
			}
		}

		return currentMinumumStageIndex;
	}

	private OutputPort<?> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final InputPort<?> inputPortOfStage = stageToBeRemoved.getInputPort();
		final IPipe<?> pipeInBetween = inputPortOfStage.getPipe();
		final OutputPort<?> distributorOutputPort = pipeInBetween.getSourcePort();
		return distributorOutputPort;
	}

	/**
	 * Returns the input port of the task farm/distributor of the task farm.
	 *
	 * @return input port of the task farm
	 */
	public InputPort<I> getInputPort() {
		return this.distributor.getInputPort();
	}

	/**
	 * Returns the output port of the task farm/merger of the task farm.
	 *
	 * @return output port of the task farm
	 */
	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

	/**
	 * Returns the first instance of the worker stages enclosed in the task farm.
	 *
	 * @return first instance of the worker stages
	 */
	public ITaskFarmDuplicable<I, O> getBasicEnclosedStage() {
		return this.enclosedStageInstances.get(0);
	}

	/**
	 * Returns a list of all currently existing worker stages in this task farm.
	 *
	 * @return list of all existing worker stages
	 */
	public List<ITaskFarmDuplicable<I, O>> getEnclosedStageInstances() {
		return this.enclosedStageInstances;
	}

	/**
	 * Returns the distributor instance of this task farm.
	 *
	 * @return distributor instance
	 */
	public DynamicDistributor<I> getDistributor() {
		return this.distributor;
	}

	/**
	 * Returns the merger instance of this task farm.
	 *
	 * @return merger instance
	 */
	public DynamicMerger<O> getMerger() {
		return this.merger;
	}

}
