package teetime.framework;

import org.apache.commons.math3.util.Pair;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import teetime.framework.pipe.DummyPipe;
import teetime.util.divideAndConquer.Identifiable;

/**
 * Represents a stage to provide functionality for the divide and conquer paradigm
 *
 * @since 2.x
 *
 * @author Robin Mohr
 *
 * @param
 * 			<P>
 *            type of elements that represent a problem to be solved.
 *
 * @param <S>
 *            type of elements that represent the solution to a problem.
 */
public abstract class AbstractDCStage<P extends Identifiable, S extends Identifiable> extends AbstractStage {

	private final int threshold;
	private boolean firstExecution;

	protected final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();

	protected final InputPort<P> inputPort = this.createInputPort();
	protected final InputPort<S> leftInputPort = this.createInputPort();
	protected final InputPort<S> rightInputPort = this.createInputPort();

	protected final OutputPort<S> outputPort = this.createOutputPort();
	protected final OutputPort<P> leftOutputPort = this.createOutputPort();
	protected final OutputPort<P> rightOutputPort = this.createOutputPort();

	/**
	 * Divide and Conquer stages need the configuration context upon creation
	 *
	 */
	public AbstractDCStage() {
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
		// check left / right input ports for new partial solutions
		checkForSolutions(rightInputPort);
		checkForSolutions(leftInputPort);
		// check main input port for new problems
		checkForProblems(inputPort);
	}

	private void checkForProblems(final InputPort<P> port) {
		P problem = port.receive();
		if (problem != null) {
			if (isBaseCase(problem)) {
				S solution = solve(problem);
				logger.trace("Sent element: " + solution.toString());
				this.getOutputPort().send(solution);
				this.terminate();
			} else {
				if (firstExecution) {
					createCopies();
				}
				Pair<P, P> tempProblems = divide(problem);
				this.getleftOutputPort().send(tempProblems.getFirst());
				this.getrightOutputPort().send(tempProblems.getSecond());
			}
		}
	}

	private void checkForSolutions(final InputPort<S> port) {
		S solution = port.receive();
		if (solution != null) {
			int solutionID = solution.getID();
			if (isInBuffer(solutionID)) {
				this.getOutputPort()
						.send(
								combine(solution, getFromBuffer(solutionID)));
				this.terminate();
			} else {
				addToBuffer(solutionID, solution);
			}
		}
	}

	private S getFromBuffer(final int solutionID) {
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
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 *
	 */
	private void createCopies() {
		makeCopy(leftOutputPort, leftInputPort);
		makeCopy(rightOutputPort, rightInputPort);
		this.firstExecution = false;
	}

	private boolean isThresholdReached() {
		return (this.threshold - this.getInstanceCount() > 0 ? false : true);
	}

	private void makeCopy(final OutputPort<P> out, final InputPort<S> in) {
		if (isThresholdReached()) {
			new DivideAndConquerRecursivePipe<P, S>(out, in);
		} else {
			final AbstractDCStage<P, S> newStage = this.duplicate();
			DynamicConfigurationContext.INSTANCE.connectPorts(out, newStage.getInputPort());
			DynamicConfigurationContext.INSTANCE.connectPorts(newStage.getOutputPort(), in);
			RuntimeServiceFacade.INSTANCE.startWithinNewThread(this, newStage);
		}
	}

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
	protected abstract Pair<P, P> divide(final P problem);

	/**
	 * Method to process the given input and send to the output port.
	 *
	 * @param element
	 *            An element to be processed
	 */
	protected abstract S solve(final P problem);

	/**
	 * Method to join the given inputs together and send to the output port.
	 *
	 * @param eLeft
	 *            First half of the resulting element.
	 * @param eRight
	 *            Second half of the resulting element.
	 */
	protected abstract S combine(final S s1, final S s2);

	/**
	 * Determines whether or not to split the input problem by examining the given element
	 *
	 * @param element
	 *            The element whose properties determine the split condition
	 */
	protected abstract boolean isBaseCase(final P problem);

	public abstract AbstractDCStage<P, S> duplicate();

	// TODO Define terminating criteria. As of now, stage terminates after first solved problem
	@Override
	public TerminationStrategy getTerminationStrategy() {
		return TerminationStrategy.BY_SELF_DECISION;
	}
}
