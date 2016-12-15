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
package teetime.framework;

import java.util.concurrent.atomic.AtomicInteger;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.AbstractDivideAndConquerSolution;
import teetime.framework.divideandconquer.DividedDCProblem;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

/**
 * A stage to solve divide and conquer problems
 *
 * @author Robin Mohr
 *
 * @param <P>
 *            type of elements that represent a problem to be solved.
 *
 * @param <S>
 *            type of elements that represent the solution to a problem.
 */
public class DivideAndConquerStage<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> extends AbstractStage {

	private final AtomicInteger numInstances;
	private int threshold;
	private boolean firstExecution;

	private final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();

	private final InputPort<P> inputPort = this.createInputPort();
	private final InputPort<S> leftInputPort = this.createInputPort();
	private final InputPort<S> rightInputPort = this.createInputPort();

	private final OutputPort<S> outputPort = this.createOutputPort();
	private final OutputPort<P> leftOutputPort = this.createOutputPort();
	private final OutputPort<P> rightOutputPort = this.createOutputPort();

	/**
	 * Creates a new divide and conquer stage and connects the additional in- and output ports with {@link teetime.framework.DivideAndConquerRecursivePipe}.
	 *
	 */
	public DivideAndConquerStage() {
		this(new AtomicInteger(0), Runtime.getRuntime().availableProcessors() - 1);
	}

	/**
	 *
	 * @param numInstances
	 *            shared atomic counter
	 * @param threshold
	 *            positive number indicated the maximal number of threads
	 */
	DivideAndConquerStage(final AtomicInteger numInstances, final int threshold) {
		new DivideAndConquerRecursivePipe<P, S>(this.leftOutputPort, this.leftInputPort);
		new DivideAndConquerRecursivePipe<P, S>(this.rightOutputPort, this.rightInputPort);
		this.numInstances = numInstances;
		// threshold should be odd: 1 for the root and each 2 for the divided instances
		this.threshold = (threshold % 2 != 0) ? threshold : threshold - 1; // NOPMD (reasonable use of the ternary operator)
		this.firstExecution = true;

		numInstances.incrementAndGet();
		logger.debug("New number of instances: {}", numInstances.get());
	}

	@Override
	protected void execute() {
		boolean receivedLeftSolution = checkForSolutions(leftInputPort);
		boolean receivedRightSolution = checkForSolutions(rightInputPort);
		boolean receivedNewProblem = checkForProblems(inputPort);
		// logger.trace("Received: {} {} {}", receivedLeftSolution, receivedRightSolution, receivedNewProblem);
		// logger.trace("Closed input ports: {} {} {}", leftInputPort.isClosed(), rightInputPort.isClosed(), inputPort.isClosed());
		if (!(receivedLeftSolution || receivedRightSolution || receivedNewProblem)) {
			if (inputPort.isClosed()) { // check explicitly when this stage is active
				// logger.debug("left: {}, right: {}", leftInputPort.isClosed(), rightInputPort.isClosed());
				leftOutputPort.getPipe().close();
				rightOutputPort.getPipe().close();
			}

			returnNoElement();
		}
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (signal instanceof TerminatingSignal && inputPort == this.inputPort) {
			leftOutputPort.getPipe().close();
			rightOutputPort.getPipe().close();
		}
		super.onSignal(signal, inputPort);
	}

	/**
	 * Sets the threshold for parallelism to the specified value.
	 *
	 * @param threshold
	 *            Number of new threads to create.
	 */
	public void setThreshold(final int threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return <code>InputPort</code>
	 */
	public final InputPort<P> getInputPort() {
		return this.inputPort;
	}

	/**
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getLeftInputPort() {
		return this.leftInputPort;
	}

	/**
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getRightInputPort() {
		return this.rightInputPort;
	}

	/**
	 * @return <code>OutputPort</code>
	 */
	public final OutputPort<S> getOutputPort() {
		return this.outputPort;
	}

	/**
	 * @return <code>OutputPort</code>
	 */
	public final OutputPort<P> getleftOutputPort() {
		return this.leftOutputPort;
	}

	/**
	 * @return <code>OutputPort</code>
	 */
	public final OutputPort<P> getrightOutputPort() {
		return this.rightOutputPort;
	}

	/**
	 * Receives and processes incoming solutions to combine or send to the next stage.
	 *
	 * @param port
	 *            The <code>InputPort</code> to receive solutions from.
	 *
	 * @return
	 * 		<code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForSolutions(final InputPort<S> port) {
		final S solution = port.receive();
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
		return solution != null;
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

	/**
	 * Receives and processes incoming problems to divide or solve.
	 *
	 * @param port
	 *            The <code>InputPort</code> to receive problems from.
	 *
	 * @return
	 * 		<code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForProblems(final InputPort<P> port) {
		final P problem = port.receive();
		if (problem != null) {
			if (problem.isBaseCase()) {
				S solution = problem.baseSolve();
				this.getOutputPort().send(solution);
			} else {
				if (firstExecution) {
					createCopies();
				}
				DividedDCProblem<P> dividedProblem = problem.divide();
				this.getleftOutputPort().send(dividedProblem.leftProblem); // first recursive call
				this.getrightOutputPort().send(dividedProblem.rightProblem); // second recursive call
			}
		}
		return problem != null;
	}

	/**
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 */
	private void createCopies() {
		DivideAndConquerStageFactory.getInstance().makeCopy(leftOutputPort, leftInputPort, this);
		DivideAndConquerStageFactory.getInstance().makeCopy(rightOutputPort, rightInputPort, this);
		this.firstExecution = false;
	}

	protected boolean isThresholdReached() {
		return threshold - numInstances.get() <= 0;
	}

	AtomicInteger getNumInstances() { // NOPMD (package-private)
		return numInstances;
	}

	int getThreshold() { // NOPMD (package-private)
		return threshold;
	}
}
