package teetime.framework;

import teetime.framework.signal.ISignal;
import teetime.framework.signal.TerminatingSignal;
import teetime.util.divideAndConquer.DividedDCProblem;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

/**
 * Represents a stage to provide functionality for the divide and conquer paradigm
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

	protected final InputPort<P> inputPort = this.createInputPort();
	protected final InputPort<S> leftInputPort = this.createInputPort();
	protected final InputPort<S> rightInputPort = this.createInputPort();

	protected final OutputPort<S> outputPort = this.createOutputPort();
	protected final OutputPort<P> leftOutputPort = this.createOutputPort();
	protected final OutputPort<P> rightOutputPort = this.createOutputPort();

	public DivideAndConquerStage() {
		new DivideAndConquerRecursivePipe<P, S>(this.leftOutputPort, this.leftInputPort);
		new DivideAndConquerRecursivePipe<P, S>(this.rightOutputPort, this.rightInputPort);
		this.threshold = Runtime.getRuntime().availableProcessors();
		this.firstExecution = true;
		this.solutionsSent = 0;
		this.problemsReceived = 0;
	}

	public void setThreshold(final int threshold) {
		this.threshold = threshold;
	}

	public final InputPort<P> getInputPort() {
		return this.inputPort;
	}

	public final InputPort<S> getLeftInputPort() {
		return this.leftInputPort;
	}

	public final InputPort<S> getRightInputPort() {
		return this.rightInputPort;
	}

	public final OutputPort<S> getOutputPort() {
		return this.outputPort;
	}

	public final OutputPort<P> getleftOutputPort() {
		return this.leftOutputPort;
	}

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

	public final DivideAndConquerStage<P, S> duplicate() {
		return new DivideAndConquerStage<P, S>();
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
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
