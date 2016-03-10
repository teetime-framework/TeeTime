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
package teetime.framework;

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

	private int threshold;
	private boolean firstExecution;
	private int problemsReceived;
	private int solutionsSent;

	private boolean signalsSent;

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
		new DivideAndConquerRecursivePipe<P, S>(this.leftOutputPort, this.leftInputPort);
		new DivideAndConquerRecursivePipe<P, S>(this.rightOutputPort, this.rightInputPort);
		this.threshold = Runtime.getRuntime().availableProcessors();
		this.firstExecution = true;
		this.solutionsSent = 0;
		this.problemsReceived = 0;
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

	@Override
	protected void execute() {
		checkForSolutions(leftInputPort);
		checkForSolutions(rightInputPort);
		checkForProblems(inputPort);
		checkForTermination();
	}

	/**
	 * Checks whether or not to terminate this stage and all child stages.
	 * The stage will termintate if there is no more input to process, a <code>TerminatingSignal</code> has been received and all child stages are terminated.
	 */
	private void checkForTermination() {
		if (this.inputPort.isClosed() && solutionsSent > 0) { // no more input, time to terminate child stages
			if (!signalsSent && problemsReceived == solutionsSent) {
				this.getleftOutputPort().sendSignal(new TerminatingSignal());// send signal to terminate child stages first
				this.getrightOutputPort().sendSignal(new TerminatingSignal());
				this.signalsSent = true;
			}
			if (this.leftInputPort.isClosed() && this.rightInputPort.isClosed()) {// all child stages terminated
				final ISignal signal = new TerminatingSignal();
				this.getOutputPort().sendSignal(signal); // terminate stages following the DC stage
				this.returnNoElement(); // terminate this stage
			}
		}
	}

	/**
	 * Receives and processes incoming solutions to combine or send to the next stage.
	 *
	 * @param port
	 *            The <code>InputPort</code> to receive solutions from.
	 *
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
				this.solutionsSent++;
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

	/**
	 * Receives and processes incoming problems to divide or solve.
	 *
	 * @param port
	 *            The <code>InputPort</code> to receive problems from.
	 *
	 * @return
	 *         <code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForProblems(final InputPort<P> port) {
		P problem = port.receive();
		if (problem != null) {
			this.problemsReceived++;
			if (problem.isBaseCase()) {
				S solution = problem.baseSolve();
				this.getOutputPort().send(solution);
				this.solutionsSent++;
			} else {
				if (firstExecution) {
					createCopies();
				}
				DividedDCProblem<P> dividedProblem = problem.divide();
				this.getleftOutputPort().send(dividedProblem.leftProblem); // first recursive call
				this.getrightOutputPort().send(dividedProblem.rightProblem); // second recursive call
			}
		}
		return problem == null;
	}

	/**
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 *
	 */
	private void createCopies() {
		DivideAndConquerStageFactory.getInstance().makeCopy(leftOutputPort, leftInputPort, this);
		DivideAndConquerStageFactory.getInstance().makeCopy(rightOutputPort, rightInputPort, this);
		if (this.inputPort.isClosed()) {
			this.leftOutputPort.sendSignal(new TerminatingSignal());
			this.rightOutputPort.sendSignal(new TerminatingSignal());
		}
		this.firstExecution = false;
	}

	protected boolean isThresholdReached() {
		return this.threshold - this.getInstanceCount() <= 0;
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (!this.signalAlreadyReceived(signal, inputPort) && !(signal instanceof TerminatingSignal)) {
			try {
				signal.trigger(this);
			} catch (Exception e) {
				this.getOwningContext().abortConfigurationRun();
			}
			for (OutputPort<?> outputPort : getOutputPorts()) {
				outputPort.sendSignal(signal);
			}
		}
	}
}
