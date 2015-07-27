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
package teetime.stage.taskfarm;

import java.util.LinkedList;
import java.util.List;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.distributor.dynamic.DynamicDistributor;
import teetime.stage.basic.merger.dynamic.DynamicMerger;
import teetime.stage.taskfarm.adaptation.AdaptationThread;
import teetime.stage.taskfarm.monitoring.PipeMonitoringService;

/**
 * The TaskFarmStage implements the task farm parallelization pattern in
 * TeeTime. It dynamically adds CPU resources at runtime depending on
 * the current CPU load and the behavior of the enclosed stage.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends AbstractCompositeStage {

	private final List<ITaskFarmDuplicable<I, O>> enclosedStageInstances = new LinkedList<ITaskFarmDuplicable<I, O>>();

	private final DynamicDistributor<I> distributor;
	private final DynamicMerger<O> merger;

	private final TaskFarmConfiguration<I, O, T> configuration;

	private AdaptationThread adaptationThread = null;

	private final PipeMonitoringService monitoringService = new PipeMonitoringService();

	/**
	 * Constructor.
	 *
	 * @param workerStage
	 *            instance of enclosed stage
	 * @param context
	 *            current execution context
	 */
	public TaskFarmStage(final T workerStage) {
		super();

		if (null == workerStage) {
			throw new IllegalArgumentException("The constructor of a Task Farm may not be called with null as the worker stage.");
		}

		this.merger = new DynamicMerger<O>() {
			@Override
			public void onStarting() throws Exception {
				synchronized (adaptationThread) {
					if (!adaptationThread.isAlive()) {
						adaptationThread.start();
					}
				}
				super.onStarting();
			}
		};
		this.distributor = new DynamicDistributor<I>() {
			@Override
			public void onTerminating() throws Exception {
				adaptationThread.stopAdaptationThread();
				adaptationThread.join();
				super.onTerminating();
			}
		};
		this.configuration = new TaskFarmConfiguration<I, O, T>();

		if (adaptationThread == null) {
			adaptationThread = new AdaptationThread();
		}
		adaptationThread.addTaskFarm(this);

		this.init(workerStage);
	}

	private void init(final T includedStage) {
		addThreadableStage(this.merger);
		addThreadableStage(includedStage.getInputPort().getOwningStage());

		final InputPort<I> stageInputPort = includedStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort);

		final OutputPort<O> stageOutputPort = includedStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort());

		enclosedStageInstances.add(includedStage);
	}

	public InputPort<I> getInputPort() {
		return this.distributor.getInputPort();
	}

	public OutputPort<O> getOutputPort() {
		return this.merger.getOutputPort();
	}

	public TaskFarmConfiguration<I, O, T> getConfiguration() {
		return this.configuration;
	}

	public ITaskFarmDuplicable<I, O> getBasicEnclosedStage() {
		return enclosedStageInstances.get(0);
	}

	public List<ITaskFarmDuplicable<I, O>> getEnclosedStageInstances() {
		return enclosedStageInstances;
	}

	public DynamicDistributor<I> getDistributor() {
		return distributor;
	}

	public DynamicMerger<O> getMerger() {
		return merger;
	}

	public PipeMonitoringService getMonitoringService() {
		return monitoringService;
	}
}
