package teetime.framework;

import teetime.framework.divideandconquer.AbstractDivideAndConquerProblem;
import teetime.framework.divideandconquer.AbstractDivideAndConquerSolution;
import teetime.framework.divideandconquer.DividedDCProblem;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

/**
 * Represents a stage to solve divide and conquer problems
 *
 * @since 2.x
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
	 * Creates a new divide and conquer stage and connects the additional in- and output ports with recursive pipes
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
	protected void setThreshold(final int threshold) {
		this.threshold = threshold;
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
	 *            Type of input port.
	 *
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getLeftInputPort() {
		return this.leftInputPort;
	}

	/**
	 * @param <S>
	 *            Type of input port.
	 *
	 * @return <code>InputPort</code>
	 */
	public final InputPort<S> getRightInputPort() {
		return this.rightInputPort;
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

	/**
	 * @param <P>
	 *            Type of output port.
	 *
	 * @return <code>OutputPort</code>
	 */
	public final OutputPort<P> getleftOutputPort() {
		return this.leftOutputPort;
	}

	/**
	 * @param <P>
	 *            Type of output port.
	 *
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
				if (logger.isDebugEnabled()) {
					logger.debug(" left terminating signal ");
				}
				this.getleftOutputPort().sendSignal(new TerminatingSignal());// send signal to terminate child stages first
				if (logger.isDebugEnabled()) {
					logger.debug(" right terminating signal ");
				}
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
	 * @param <S>
	 *            Type of solutions.
	 * @return
	 *         <code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForSolutions(final InputPort<S> port) {
		S solution = port.receive();
		if (solution != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(" received solution " + solution.getID());
			}
			int solutionID = solution.getID();
			if (isInBuffer(solutionID)) {
				S bufferedSolution = getSolutionFromBuffer(solutionID);
				S combinedSolution = solution.combine(bufferedSolution);
				if (logger.isDebugEnabled()) {
					logger.debug(" created solution: " + combinedSolution.getID());
				}
				outputPort.send(combinedSolution);
				this.solutionsSent++;
				if (logger.isDebugEnabled()) {
					logger.debug(" solutionsSent: " + solutionsSent);
				}
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
	 * @param <P>
	 *            Type of problems.
	 * @return
	 *         <code>true</code> if there was input to receive, <code>false</code> otherwise
	 */
	private boolean checkForProblems(final InputPort<P> port) {
		P problem = port.receive();
		if (problem != null) {
			this.problemsReceived++;
			if (logger.isDebugEnabled()) {
				logger.debug(" problemsReceived: " + problemsReceived);
			}
			if (problem.isBaseCase()) {
				S solution = problem.solve();
				this.getOutputPort().send(solution);
				this.solutionsSent++;
				if (logger.isDebugEnabled()) {
					logger.debug(" solutionsSent: " + solutionsSent);
				}
			} else {
				if (firstExecution) {
					if (logger.isDebugEnabled()) {
						logger.debug(" creating copies");
					}
					createCopies();
				}
				DividedDCProblem<P> dividedProblem = problem.divide();
				if (logger.isDebugEnabled()) {
					logger.debug(" problem left out" + dividedProblem.leftProblem.getID());
				}
				this.getleftOutputPort().send(dividedProblem.leftProblem); // first recursive call
				if (logger.isDebugEnabled()) {
					logger.debug(" problem right out" + dividedProblem.rightProblem.getID());
				}
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
		DivideAndConquerStageCopier.getInstance().makeCopy(leftOutputPort, leftInputPort, this);
		DivideAndConquerStageCopier.getInstance().makeCopy(rightOutputPort, rightInputPort, this);
		if (this.inputPort.isClosed()) {
			this.leftOutputPort.sendSignal(new TerminatingSignal());
			this.rightOutputPort.sendSignal(new TerminatingSignal());
		}
		this.firstExecution = false;
	}

	protected boolean isThresholdReached() {
		return this.threshold - this.getInstanceCount() <= 0;
	}

	protected final DivideAndConquerStage<P, S> duplicate() {
		return new DivideAndConquerStage<P, S>();
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
