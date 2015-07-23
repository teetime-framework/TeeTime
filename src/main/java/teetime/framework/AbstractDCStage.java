package teetime.framework;

import teetime.stage.taskfarm.ITaskFarmDuplicable;

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
public abstract class AbstractDCStage<P, S> extends AbstractStage implements ITaskFarmDuplicable<P, S> { // FIXME check compatibility of interface

	private final IntObjectMap<S> solutionBuffer = new IntObjectHashMap<S>();
	private final DynamicConfigurationContext context;

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
	public AbstractDCStage(final DynamicConfigurationContext context) {
		if (null == context) {
			throw new IllegalArgumentException("Context may not be null.");
		}
		this.context = context;
	}

	@Override
	public final InputPort<P> getInputPort() {
		return this.inputPort;
	}

	public final InputPort<S> getLeftInputPort() {
		return this.leftInputPort;
	}

	public final InputPort<S> getRightInputPort() {
		return this.rightInputPort;
	}

	@Override
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
	protected final void executeStage() {
		// TODO
	}

	/**
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 *
	 */
	private void makeCopy(final OutputPort<P> out, final InputPort<S> in) {
		final AbstractDCStage<P, S> newStage = this;
		context.connectPorts(out, newStage.getInputPort());
		context.connectPorts(newStage.getOutputPort(), in);
		context.beginThread(newStage);
		context.sendSignals(out);
	}

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
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
	protected abstract void combine(final S s1, final S s2);

	/**
	 * Determines whether or not to split the input problem by examining the given element
	 *
	 * @param element
	 *            The element whose properties determine the split condition
	 */
	protected abstract boolean isBaseCase(final P problem);

	@Override
	public abstract ITaskFarmDuplicable<P, S> duplicate();

	public DynamicConfigurationContext getContext() {
		return this.context;
	}
}
