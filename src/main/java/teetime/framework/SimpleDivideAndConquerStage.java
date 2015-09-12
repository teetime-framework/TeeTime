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
import teetime.framework.divideandconquer.DividedDCProblem;

/**
 * A simple stage to solve divide and conquer problems
 *
 * @author Robin Mohr
 *
 * @param <P>
 *            type of elements that represent a problem to be solved.
 *
 * @param <S>
 *            type of elements that represent the solution to a problem.
 */
public class SimpleDivideAndConquerStage<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> extends AbstractStage {

	private final InputPort<P> inputPort = this.createInputPort();
	private final OutputPort<S> outputPort = this.createOutputPort();

	public SimpleDivideAndConquerStage() {
		super();
	}

	/**
	 * @param <P>
	 *            Type of input port.
	 *
	 * @return <code>InputPort</code>
	 */
	public final InputPort<P> getInputPort() {
		return this.inputPort;
	}

	/**
	 * @param <S>
	 *            Type of output port.
	 *
	 * @return <code>OutputPort</code>
	 */
	public final OutputPort<S> getOutputPort() {
		return this.outputPort;
	}

	@Override
	protected void execute() {
		P inputProblem = this.getInputPort().receive();
		if (inputProblem != null) {
			this.getOutputPort().send(this.divideAndConquer(inputProblem));
		} else {
			returnNoElement();
		}
	}

	private S divideAndConquer(final P problem) {
		if (problem.isBaseCase()) {
			return problem.solve();
		} else {
			DividedDCProblem<P> dividedProblem = problem.divide();
			S firstSolution = divideAndConquer(dividedProblem.leftProblem); // recursive call
			S secondSolution = divideAndConquer(dividedProblem.rightProblem); // recursive call
			return firstSolution.combine(secondSolution);
		}
	}

}
