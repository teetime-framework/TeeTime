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
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

/**
 * A stage to solve divide and conquer problems
 *
 * @author Robin Mohr, Christian Wulf
 *
 * @param <P>
 *            type of elements that represent a problem to be solved.
 *
 * @param <S>
 *            type of elements that represent the solution to a problem.
 */
public class DivideAndConquerStage<P extends AbstractDivideAndConquerProblem<P, S>, S extends AbstractDivideAndConquerSolution<S>> extends AbstractStage {

	private static final int DEFAULT_THRESHOLD = Runtime.getRuntime().availableProcessors();

	private final AtomicInteger numCopiedInstances;
	private final int maxCopiedInstances;
	private boolean firstExecution;

	private final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();

	private final InputPort<P> inputPort = this.createInputPort();
	private final InputPort<S> leftInputPort = this.createInputPort();
	private final InputPort<S> rightInputPort = this.createInputPort();

	private final OutputPort<S> outputPort = this.createOutputPort();
	private final OutputPort<P> leftOutputPort = this.createOutputPort();
	private final OutputPort<P> rightOutputPort = this.createOutputPort();

	/**
	 * Creates a new divide and conquer stage and connects the additional in- and output ports with {@link teetime.framework.DivideAndConquerRecursivePipe} with a
	 * default threshold of {@code Runtime.getRuntime().availableProcessors()}.
	 */
	public DivideAndConquerStage() {
		this(DEFAULT_THRESHOLD);
	}

	/**
	 * Creates a new divide and conquer stage and connects the additional in- and output ports with {@link teetime.framework.DivideAndConquerRecursivePipe}.
	 */
	public DivideAndConquerStage(final int maxCopiedInstances) {
		this(new AtomicInteger(-1), maxCopiedInstances);
	}

	/**
	 *
	 * @param numCopiedInstances
	 *            shared atomic counter
	 * @param maxCopiedInstances
	 *            positive number indicated the maximal number of threads
	 */
	DivideAndConquerStage(final AtomicInteger numCopiedInstances, final int maxCopiedInstances) {
		new DivideAndConquerRecursivePipe<P, S>(this.leftOutputPort, this.leftInputPort);
		new DivideAndConquerRecursivePipe<P, S>(this.rightOutputPort, this.rightInputPort);
		this.numCopiedInstances = numCopiedInstances;
		this.maxCopiedInstances = maxCopiedInstances;
		this.firstExecution = true;

		numCopiedInstances.incrementAndGet();
		logger.debug("New number of instances: {}", numCopiedInstances.get());
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
				// leftOutputPort.sendSignal(new TerminatingSignal());
				// rightOutputPort.sendSignal(new TerminatingSignal());
			}

			returnNoElement();
		}
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (signal instanceof TerminatingSignal && inputPort == this.inputPort) {
			leftOutputPort.getPipe().close();
			rightOutputPort.getPipe().close();
			// leftOutputPort.sendSignal(signal);
			// rightOutputPort.sendSignal(signal);
		}
		super.onSignal(signal, inputPort);
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

	private void createCopies() {
		if (isThresholdReached()) {
			new DivideAndConquerRecursivePipe<P, S>(leftOutputPort, leftInputPort);
			new DivideAndConquerRecursivePipe<P, S>(rightOutputPort, rightInputPort);
		} else {
			copy(leftOutputPort, leftInputPort, this);
			copy(rightOutputPort, rightInputPort, this);
		}

		this.firstExecution = false;
	}

	private void copy(final OutputPort<P> outputPort, final InputPort<S> inputPort, final DivideAndConquerStage<P, S> callingStage) {
		DivideAndConquerStage<P, S> newStage = new DivideAndConquerStage<P, S>(numCopiedInstances, maxCopiedInstances);
		DynamicConfigurationContext.INSTANCE.connectPorts(outputPort, newStage.getInputPort());
		DynamicConfigurationContext.INSTANCE.connectPorts(newStage.getOutputPort(), inputPort);
		outputPort.sendSignal(new StartingSignal());
		RuntimeServiceFacade.INSTANCE.startWithinNewThread(callingStage, newStage);
	}

	private boolean isThresholdReached() {
		return maxCopiedInstances - numCopiedInstances.get() <= 0;
	}

}
