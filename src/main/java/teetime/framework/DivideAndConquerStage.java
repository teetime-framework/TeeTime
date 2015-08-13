package teetime.framework;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

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

	private final int threshold;
	private boolean firstExecution;

	private boolean signalsSent = false;

	protected final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();

	protected final InputPort<P> inputPort = this.createInputPort();
	protected final InputPort<S> leftInputPort = this.createInputPort();
	protected final InputPort<S> rightInputPort = this.createInputPort();

	protected final OutputPort<S> outputPort = this.createOutputPort();
	protected final OutputPort<P> leftOutputPort = this.createOutputPort();
	protected final OutputPort<P> rightOutputPort = this.createOutputPort();

	public DivideAndConquerStage() {
		leftInputPort.setPipe(DummyPipe.INSTANCE);
		rightInputPort.setPipe(DummyPipe.INSTANCE);
		this.threshold = Runtime.getRuntime().availableProcessors();
		this.firstExecution = true;
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
		checkForSolutions(rightInputPort);
		checkForSolutions(leftInputPort);
		checkForProblems(inputPort);
		if (this.inputPort.isClosed()) { // no more input, time to terminate child stages
			if (!signalsSent) {
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

	private boolean checkForProblems(final InputPort<P> port) {
		P problem = port.receive();
		if (problem != null) {
			if (problem.isBaseCase()) {
				S solution = problem.solve();
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
		return problem == null;
	}

	/**
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 *
	 */
	private void createCopies() {
		makeCopy(leftOutputPort, leftInputPort);
		makeCopy(rightOutputPort, rightInputPort);
		this.firstExecution = false;
	}

	private boolean isThresholdReached() {
		return this.threshold - this.getInstanceCount() <= 0;
	}

	private void makeCopy(final OutputPort<P> outputPort, final InputPort<S> inputPort) {
		if (isThresholdReached()) {
			new DivideAndConquerRecursivePipe<P, S>(outputPort, inputPort);
		} else {
			final DivideAndConquerStage<P, S> newStage = this.duplicate();
			DynamicConfigurationContext.INSTANCE.connectPorts(outputPort, newStage.getInputPort());
			DynamicConfigurationContext.INSTANCE.connectPorts(newStage.getOutputPort(), inputPort);
			outputPort.sendSignal(new StartingSignal());
			RuntimeServiceFacade.INSTANCE.startWithinNewThread(this, newStage);
		}
	}

	public DivideAndConquerStage<P, S> duplicate() {
		return new DivideAndConquerStage<P, S>();
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (!this.signalAlreadyReceived(signal, inputPort)) {
			if (signal instanceof TerminatingSignal) {
				// do nothing
			} else {
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
}
