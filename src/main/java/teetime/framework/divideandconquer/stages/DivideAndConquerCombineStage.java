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
package teetime.framework.divideandconquer.stages;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.AbstractDivideAndConquerSolution;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

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
public class DivideAndConquerCombineStage<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> extends AbstractStage {

	private final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();

	private final InputPort<S> firstInputPort = this.createInputPort();
	private final InputPort<S> secondInputPort = this.createInputPort();
	private final OutputPort<S> outputPort = this.createOutputPort();

	public DivideAndConquerCombineStage() {
		super();
	}

	/**
	 * @param <S>
	 *            Type of input port.
	 *
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getFirstInputPort() {
		return this.firstInputPort;
	}

	/**
	 * @param <S>
	 *            Type of input port.
	 *
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getSecondInputPort() {
		return this.secondInputPort;
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
		if (checkForSolutions(firstInputPort) && checkForSolutions(secondInputPort)) {
			returnNoElement();
		}
	}

	/**
	 * Receives and processes incoming solutions to combine or send to the next stage.
	 *
	 * @param port
	 *            The <code>InputPort</code> to receive solutions from.
	 *
	 * @param <S>
	 *            Type of solutions.
	 * @return
	 *         <code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForSolutions(final InputPort<S> port) {
		S solution = port.receive();
		if (solution != null) {
			int solutionID = solution.getID();
			if (isInBuffer(solutionID)) {
				S bufferedSolution = getSolutionFromBuffer(solutionID);
				S combinedSolution = solution.combine(bufferedSolution);
				outputPort.send(combinedSolution);
			} else {
				addToBuffer(solutionID, solution);
			}
		}
		return solution == null;
	}

	private S getSolutionFromBuffer(final int solutionID) {
		S tempSolution = this.solutionBuffer.get(solutionID);
		this.solutionBuffer.remove(solutionID);
		return tempSolution;
	}

	private void addToBuffer(final int solutionID, final S solution) {
		this.solutionBuffer.put(solutionID, solution);
	}

	private boolean isInBuffer(final int solutionID) {
		return this.solutionBuffer.containsKey(solutionID);
	}

}
