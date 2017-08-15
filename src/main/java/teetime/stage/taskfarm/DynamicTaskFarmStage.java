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
package teetime.stage.taskfarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.RuntimeServiceFacade;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.framework.pipe.IPipe;
import teetime.stage.basic.distributor.dynamic.CreatePortActionDistributor;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.distributor.dynamic.RemovePortActionDistributor;
import teetime.stage.basic.merger.dynamic.CreatePortActionMerger;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.basic.merger.strategy.SkippingBusyWaitingRoundRobinStrategy;
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
public class DynamicTaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends StaticTaskFarmStage<I, O, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicTaskFarmStage.class);

	/** configuration of the task farm **/
	private final TaskFarmConfiguration<I, O, T> configuration = new TaskFarmConfiguration<I, O, T>();

	/**
	 * Creates a task farm using <i>n</i> worker stages with a pipe capacity of 100, where <i>n</i> is
	 *
	 * <pre>
	 * Runtime.getRuntime().availableProcessors()
	 * </pre>
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 */
	// public DynamicTaskFarmStage(final T workerStage) {
	// this(workerStage, Runtime.getRuntime().availableProcessors(), 100);
	// }

	/**
	 * Creates a task farm using a worker stage with a pipe capacity of 100.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 * @param initialNumOfStages
	 *            the initial number of stages used by the task farm
	 */
	public DynamicTaskFarmStage(final T workerStage, final int initialNumOfStages) {
		this(workerStage, initialNumOfStages, 100);
	}

	/**
	 * Creates a task farm using a worker stage with a given pipe capacity.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 * @param pipeCapacity
	 *            pipe capacity to be used
	 * @param initialNumOfStages
	 *            the initial number of stages used by the task farm
	 */
	public DynamicTaskFarmStage(final T workerStage, final int initialNumOfStages, final int pipeCapacity) {
		super(workerStage, initialNumOfStages, pipeCapacity, new DynamicDistributor<I>(), new DynamicMerger<O>(new SkippingBusyWaitingRoundRobinStrategy()));

		// for (ITaskFarmDuplicable<I, O> workerStage : getWorkerStages()) {
		// includedStage.setTaskFarmStage(this);
		// }

		this.configuration.setPipeCapacity(pipeCapacity);
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
			LOGGER.debug("Adding stage (current amount of stages: {})", getWorkerStages().size());
		}

		if (!getMerger().isActive()) {
			getMerger().declareActive();
		}

		AbstractStage basicEnclosedStage = getBasicEnclosedStage().getInputPort().getOwningStage();
		if (!basicEnclosedStage.isActive()) {
			basicEnclosedStage.declareActive();
		}

		final ITaskFarmDuplicable<I, O> newStage = getBasicEnclosedStage().duplicate();
		// newStage.setTaskFarmStage(this);

		final CreatePortActionDistributor<I> distributorPortAction = new CreatePortActionDistributor<I>(newStage.getInputPort(),
				getPipeCapacity());
		getDistributor().addPortActionRequest(distributorPortAction);

		distributorPortAction.waitForCompletion();

		final CreatePortActionMerger<O> mergerPortAction = new CreatePortActionMerger<O>(newStage.getOutputPort(),
				getPipeCapacity());
		getMerger().addPortActionRequest(mergerPortAction);

		mergerPortAction.waitForCompletion();

		// the validating and the starting signal is sent by the create action
		RuntimeServiceFacade.INSTANCE.startWithinNewThread(getDistributor(), newStage.getInputPort().getOwningStage());

		getWorkerStages().add(newStage);

		// TODO add event "new stage added" to enable monitoring of the new pipe (see #addNewPipeToMonitoring)

		return newStage;
	}

	// private void addNewPipeToMonitoring(final ITaskFarmDuplicable<I, O> newStage) {
	// if (this.taskFarmStage.getConfiguration().isMonitoringEnabled()) {
	// try {
	// this.taskFarmStage.getPipeMonitoringService().addMonitoredItem((IMonitorablePipe) newStage.getInputPort().getPipe());
	// } catch (ClassCastException e) {
	// throw new TaskFarmControllerException("A generated pipe is not monitorable.", e);
	// }
	// }
	// }

	/**
	 * Dynamically removes a stage from the controlled task farm.
	 *
	 * @throws InterruptedException
	 *
	 * @author Christian Claus Wiechmann, Christoph Dornieden (code moved from TaskFarmController)
	 */
	public ITaskFarmDuplicable<I, O> removeStageAtRuntime() throws InterruptedException {
		if (getWorkerStages().size() == 1) {
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Removing stage (current amount of stages: {})", getWorkerStages().size());
		}

		ITaskFarmDuplicable<I, O> stageToBeRemoved = getWorkerStages().get(getStageIndexWithLeastRemainingInput());
		OutputPort<? extends I> distributorOutputPort = this.getRemoveableDistributorOutputPort(stageToBeRemoved);

		final RemovePortActionDistributor<I> distributorPortAction = new RemovePortActionDistributor<I>(distributorOutputPort);
		getDistributor().addPortActionRequest(distributorPortAction);
		getWorkerStages().remove(stageToBeRemoved);

		distributorPortAction.waitForCompletion();

		return stageToBeRemoved;
	}

	// FIXME the task farm itself should not choose which stage to remove.
	// Instead, a strategy from outside the task farm should determine.
	private int getStageIndexWithLeastRemainingInput() {
		int currentMinimum = Integer.MAX_VALUE; // NOPMD (DU: caused by loop)
		int currentMinumumStageIndex = getWorkerStages().size() - 1;// NOPMD (DU: caused by loop)

		// do not remove basic stage
		for (int i = 1; i < getWorkerStages().size(); i++) {
			final ITaskFarmDuplicable<I, O> instance = getWorkerStages().get(i);
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

	private OutputPort<I> getRemoveableDistributorOutputPort(final ITaskFarmDuplicable<I, O> stageToBeRemoved) {
		final InputPort<I> inputPortOfStage = stageToBeRemoved.getInputPort();
		final IPipe<?> pipeInBetween = inputPortOfStage.getPipe();
		@SuppressWarnings("unchecked")
		final OutputPort<I> distributorOutputPort = (OutputPort<I>) pipeInBetween.getSourcePort();
		return distributorOutputPort; // NOPMD (UnnecessaryLocalBeforeReturn: annotation should be declared at the trigger)
	}

	/**
	 * Returns the first instance of the worker stages enclosed in the task farm.
	 *
	 * @return first instance of the worker stages
	 */
	public ITaskFarmDuplicable<I, O> getBasicEnclosedStage() {
		return this.getWorkerStages().get(0);
	}

	/**
	 * @return the distributor instance of this task farm.
	 */
	@Override
	/* default */ DynamicDistributor<I> getDistributor() { // (Used in tests only; hence declared package-private)
		return (DynamicDistributor<I>) super.getDistributor();
	}

	/**
	 * @return the merger instance of this task farm.
	 */
	@Override
	/* default */ DynamicMerger<O> getMerger() { // (Used in tests only; hence declared package-private)
		return (DynamicMerger<O>) super.getMerger();
	}

	public TaskFarmConfiguration<I, O, T> getConfiguration() {
		return configuration;
	}

}
