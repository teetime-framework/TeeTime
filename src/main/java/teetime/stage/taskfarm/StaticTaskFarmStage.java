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

import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
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
public class StaticTaskFarmStage<I, O, T extends ITaskFarmDuplicable<I, O>> extends CompositeStage {

	private static final int DEFAULT_PIPE_CAPACITY = 100;

	private final Distributor<I> distributor = new Distributor<I>();
	private final Merger<O> merger = new Merger<O>();

	/**
	 * Create a task farm using <code>n</code> worker stages each with a pipe capacity of 100.
	 * <code>x</code> represents the number of available processors given by
	 *
	 * <pre>
	 * Runtime.getRuntime().availableProcessors()
	 * </pre>
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 */
	public StaticTaskFarmStage(final T workerStage) {
		this(workerStage, Runtime.getRuntime().availableProcessors(), DEFAULT_PIPE_CAPACITY);
	}

	/**
	 * Create a task farm using a worker stage with a given pipe capacity.
	 *
	 * @param workerStage
	 *            stage to be parallelized by the task farm
	 * @param pipeCapacity
	 *            pipe capacity to be used
	 */
	public StaticTaskFarmStage(final T workerStage, final int numberStages, final int pipeCapacity) {
		super();
		if (null == workerStage) {
			throw new IllegalArgumentException("The constructor of a Task Farm may not be called with null as the worker stage.");
		}
		if (numberStages < 1) { // NOPMD
			throw new IllegalArgumentException("The number of worker stages must be at least 1.");
		}
		this.init(workerStage, numberStages, pipeCapacity);
	}

	private void init(final T includedStage, final int numberStages, final int pipeCapacity) {
		for (int i = 0; i < numberStages; i++) {
			ITaskFarmDuplicable<I, O> workerStage = includedStage.duplicate();

			connectWorkerStage(workerStage, pipeCapacity);
			workerStage.getInputPort().getOwningStage().declareActive();
		}
		this.merger.declareActive();
	}

	private void connectWorkerStage(final ITaskFarmDuplicable<I, O> workerStage, final int pipeCapacity) {
		final InputPort<I> stageInputPort = workerStage.getInputPort();
		connectPorts(this.distributor.getNewOutputPort(), stageInputPort, pipeCapacity);

		final OutputPort<O> stageOutputPort = workerStage.getOutputPort();
		connectPorts(stageOutputPort, this.merger.getNewInputPort(), pipeCapacity);
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

}
