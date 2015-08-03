package teetime.framework;

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
public abstract class AbstractDCStage<P extends Identifiable, S extends Identifiable> extends AbstractStage { // FIXME check compatibility of interface
																												// ITASKFARMDUPLICABLE
																												// FIXME this is needed for unconnected input ports
																												// at the moment. framework won't validate additional
																												// input ports on validating
	// TODO thread-optimization and scheduling: use these to create new threads / stages efficiently
	private final int threshold = Runtime.getRuntime().availableProcessors();
	private final int numberOfStages = 1;

	protected final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();
	private final DynamicConfigurationContext context = new DynamicConfigurationContext();

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
		// FIXME maybe validate input ports in addition to output ports.
		leftInputPort.setPipe(DummyPipe.INSTANCE);
		rightInputPort.setPipe(DummyPipe.INSTANCE);
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
		checkPort(rightInputPort);
		checkPort(leftInputPort);

		// check main input port for new problems
		P problem = this.getInputPort().receive();
		if (problem == null) {
		} else {
			if (isBaseCase(problem)) {
				S solution = solve(problem);
				logger.trace("Sent element: " + solution.toString());
				this.getOutputPort().send(solution);
				this.terminate();
			} else {
				makeCopy(leftOutputPort, leftInputPort);
				makeCopy(rightOutputPort, rightInputPort);
				divide(problem);
			}
		}
	}

	// TODO make function more generic to check all 3 ports with different params
	private void checkPort(final InputPort<S> port) {
		S solution = port.receive();
		if (solution == null) {
		} else {
			int solutionID = solution.getID();
			if (isInBuffer(solutionID)) {
				combine(solution, getFromBuffer(solutionID));
				logger.trace("Sent element: " + solution.toString());
				this.getOutputPort().send(solution);
				this.terminate();
			} else {
				addToBuffer(solutionID, solution);
			}
		}

	}

	// FIXME maybe write an own class for the buffer, outsource these functions
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
	// TODO thread intantiation and scheduling.... optimization
	private void makeCopy(final OutputPort<P> out, final InputPort<S> in) {
		final AbstractDCStage<P, S> newStage = this.duplicate();
		context.connectPorts(out, newStage.getInputPort());
		context.connectPorts(newStage.getOutputPort(), in);
		context.beginThread(this, newStage);
		context.sendSignals(out);
	}

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
	// FIXME change return type from void to (p1,p2) <- two problems
	// so the sending can be done here instead of the quicksortstage
	protected abstract void divide(final P problem);

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
	// FIXME change return type to Solution
	protected abstract void combine(final S s1, final S s2);

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
