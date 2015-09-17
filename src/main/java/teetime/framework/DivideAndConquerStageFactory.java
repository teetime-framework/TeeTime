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
package teetime.framework;

import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.AbstractDivideAndConquerSolution;
import teetime.framework.signal.StartingSignal;

/**
 * Used by {@link teetime.framework.DivideAndConquerStage} for thread-safe instantiation.
 *
 * @author Robin Mohr
 *
 */
public class DivideAndConquerStageFactory {
	private DivideAndConquerStageFactory() {}

	/**
	 * This inner class is used for thread-safe initialization of the {@link teetime.framework.divideandconquer.DivideAndConquerStageFactory}
	 */
	private static class Initialization {
		private static final DivideAndConquerStageFactory INSTANCE = new DivideAndConquerStageFactory();
	}

	/**
	 * Returns the instance or creates a new one if none is present.
	 */
	public static DivideAndConquerStageFactory getInstance() {
		return Initialization.INSTANCE;
	}

	/**
	 * Receives and processes incoming problems to divide or solve.
	 *
	 * @param inputPort
	 *            The <code>InputPort</code> to connect the new stage to.
	 *
	 * @param outputPort
	 *            The <code>OutputPort</code> to connect the new stage to.
	 *
	 * @param callingStage
	 *            The existing Stage to connect the new Stage to.
	 *
	 * @param <P>
	 *            Type of problem.
	 * @param <S>
	 *            Type of solution.
	 *
	 */
	protected synchronized <P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> void makeCopy(
			final OutputPort<P> outputPort,
			final InputPort<S> inputPort, final DivideAndConquerStage<P, S> callingStage) {
		if (callingStage.isThresholdReached()) {
			new DivideAndConquerRecursivePipe<P, S>(outputPort, inputPort);
		} else {
			DivideAndConquerStage<P, S> newStage = new DivideAndConquerStage<P, S>();
			DynamicConfigurationContext.INSTANCE.connectPorts(outputPort, newStage.getInputPort());
			DynamicConfigurationContext.INSTANCE.connectPorts(newStage.getOutputPort(), inputPort);
			outputPort.sendSignal(new StartingSignal());
			RuntimeServiceFacade.INSTANCE.startWithinNewThread(callingStage, newStage);
		}
	}
}
