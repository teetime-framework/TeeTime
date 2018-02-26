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

import java.util.ArrayList;
import java.util.List;

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.basic.ITransformation;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

/**
 * Represents the task farm parallelization pattern in TeeTime.
 *
 * @author Christian Claus Wiechmann, Christian Wulf
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of the parallelized stage
 */
public class StaticTaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends CompositeStage implements ITransformation<I, O> {

	private static final int MAX_NUMBER_OF_STAGES = Runtime.getRuntime().availableProcessors();

	private final Distributor<I> distributor;
	private final Merger<O> merger;
	/** List of all currently existing worker stages */
	private final List<ITaskFarmDuplicable<I, O>> workerStages;

	private InputPort<I> inputPort;
	private OutputPort<O> outputPort;

	/**
	 * Creates a task farm stage with <i>n</i> worker stages and a pipe capacity of {@value #DEFAULT_PIPE_CAPACITY}, where <i>n</i>
	 * is
	 *
	 * <pre>
	 * Runtime.getRuntime().availableProcessors()
	 * </pre>
	 *
	 * @param workerStage
	 */
	public StaticTaskFarmStage(final T workerStage) {
		this(workerStage, MAX_NUMBER_OF_STAGES, DEFAULT_PIPE_CAPACITY);
	}

	public StaticTaskFarmStage(final T workerStage, final int numberStages) {
		this(workerStage, numberStages, DEFAULT_PIPE_CAPACITY);
	}

	public StaticTaskFarmStage(final T workerStage, final int numberStages, final int pipeCapacity) {
		this(workerStage, numberStages, pipeCapacity, new Distributor<I>(), new Merger<O>());
	}

	protected StaticTaskFarmStage(final T workerStage, final int numberStages, final int pipeCapacity, final Distributor<I> distributor, final Merger<O> merger) {
		super();
		if (null == workerStage) {
			throw new IllegalArgumentException("The constructor of a Task Farm may not be called with null as the worker stage.");
		}
		if (numberStages < 1) {
			throw new IllegalArgumentException("The number of worker stages must be at least 1.");
		}
		if (pipeCapacity < 1) {
			throw new IllegalArgumentException("The capacity of the pipe(s) must be at least 1.");
		}
		this.distributor = distributor;
		this.merger = merger;
		this.workerStages = new ArrayList<ITaskFarmDuplicable<I, O>>();

		this.init(workerStage, numberStages, pipeCapacity);
	}

	private void init(final T workerStage, final int numberStages, final int pipeCapacity) {
		connectWorkerStage(workerStage, pipeCapacity);
		workerStage.getInputPort().getOwningStage().declareActive();

		for (int i = 1; i < numberStages; i++) {
			ITaskFarmDuplicable<I, O> duplicatedWorkerStage = workerStage.duplicate();

			connectWorkerStage(duplicatedWorkerStage, pipeCapacity);
			duplicatedWorkerStage.getInputPort().getOwningStage().declareActive();
		}

		if (numberStages > 1) {
			this.merger.declareActive();
		}

		// map outer ports to inner ports
		inputPort = createInputPort(this.distributor.getInputPort());
		outputPort = createOutputPort(this.merger.getOutputPort());
	}

	private void connectWorkerStage(final ITaskFarmDuplicable<I, O> workerStage, final int pipeCapacity) {
		final InputPort<I> stageInputPort = workerStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort, pipeCapacity);

		final OutputPort<O> stageOutputPort = workerStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort(), pipeCapacity);

		this.workerStages.add(workerStage);
	}

	/**
	 * Returns the input port of the task farm/distributor of the task farm.
	 *
	 * @return input port of the task farm
	 */
	@Override
	public InputPort<I> getInputPort() {
		return inputPort;
	}

	/**
	 * Returns the output port of the task farm/merger of the task farm.
	 *
	 * @return output port of the task farm
	 */
	@Override
	public OutputPort<O> getOutputPort() {
		return outputPort;
	}

	// /**
	// * Declares the internal distributor to be executed by an own thread.
	// */
	// @Override
	// public void declareActive() {
	// distributor.declareActive();
	// }
	//
	// @Override
	// public StageState getCurrentState() {
	// return distributor.getCurrentState();
	// }

	/* default */ Distributor<I> getDistributor() {
		return distributor;
	}

	/* default */ Merger<O> getMerger() {
		return merger;
	}

	protected int getPipeCapacity() {
		return distributor.getOutputPorts().get(0).getPipe().capacity();
	}

	/**
	 * @return a list of all currently existing worker stages
	 */
	public List<ITaskFarmDuplicable<I, O>> getWorkerStages() {
		return workerStages;
	}

}
