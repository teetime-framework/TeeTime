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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.taskfarm.adaptation.AdaptationThread;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

/**
 * Represents the self-adaptive task farm parallelization pattern in
 * TeeTime. It dynamically adds CPU resources at runtime depending on
 * the current CPU load and the behavior of the parallelized stage.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of the parallelized stage
 */
public final class TaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends CompositeStage {

	/** currently existing worker stages **/
	private final List<ITaskFarmDuplicable<I, O>> enclosedStageInstances = new LinkedList<ITaskFarmDuplicable<I, O>>();

	/** merger instance **/
	private final DynamicDistributor<I> distributor = new DynamicDistributor<I>();
	/** distributor instance **/
	private final DynamicMerger<O> merger;

	/** configuration of the task farm **/
	private final TaskFarmConfiguration<I, O, T> configuration = new TaskFarmConfiguration<I, O, T>();

	/** adaptation thread belonging to this task farm **/
	private final AdaptationThread<I, O, T> adaptationThread;

	/** monitoring service regarding pipe throughputs **/
	private final PipeMonitoringService pipeMonitoringService;
	/** monitoring service regarding performance of the whole task farm **/
	private final SingleTaskFarmMonitoringService taskFarmMonitoringService;

	/**
	 * Create a task farm using a worker stage with a pipe capacity of 100.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 */
	public TaskFarmStage(final T workerStage) {
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
	public TaskFarmStage(final T workerStage, final int pipeCapacity) {
		this(workerStage, null, pipeCapacity);
	}

	TaskFarmStage(final T workerStage, final DynamicMerger<O> merger, final int pipeCapacity) {
		super();

		if (null == workerStage) {
			throw new IllegalArgumentException("The constructor of a Task Farm may not be called with null as the worker stage.");
		}

		if (merger == null) {
			this.merger = new DynamicMerger<O>() {
				@Override
				public void onStarting() throws Exception {
					adaptationThread.start();
					super.onStarting();
				}

				@Override
				public void onTerminating() throws Exception {
					adaptationThread.stopAdaptationThread();
					super.onTerminating();
				}
			};
		} else {
			this.merger = merger;
		}

		this.adaptationThread = new AdaptationThread<I, O, T>(this);
		this.taskFarmMonitoringService = new SingleTaskFarmMonitoringService(this, this.adaptationThread.getHistoryService());
		this.pipeMonitoringService = new PipeMonitoringService(this.adaptationThread.getHistoryService());

		this.configuration.setPipeCapacity(pipeCapacity);

		this.init(workerStage);
	}

	private void init(final T includedStage) {
		final InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort, this.configuration.getPipeCapacity());

		final OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort(), this.configuration.getPipeCapacity());

		this.merger.declareActive();
		includedStage.getInputPort().getOwningStage().declareActive();

		this.enclosedStageInstances.add(includedStage);
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
	 * Returns the configuration parameters of the task farm.
	 *
	 * @return configuration parameters
	 */
	public TaskFarmConfiguration<I, O, T> getConfiguration() {
		return this.configuration;
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

	/**
	 * Returns the monitoring service of this task farm regarding pipe throughputs.
	 *
	 * @return monitoring service regarding pipe throughputs
	 */
	public PipeMonitoringService getPipeMonitoringService() {
		return this.pipeMonitoringService;
	}

	/**
	 * Returns the monitoring service of this task farm regarding performance of the whole task farm.
	 *
	 * @return monitoring service regarding performance of the whole task farm
	 */
	public SingleTaskFarmMonitoringService getTaskFarmMonitoringService() {
		return this.taskFarmMonitoringService;
	}
}
