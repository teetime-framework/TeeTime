package teetime.framework;

/**
 * Represents a stage to provide functionality for the divide and conquer paradigm
 *
 * @since 2.x
 *
 * @author Christian Wulf, Nelson Tavares de Sousa, Robin Mohr
 *
 * @param <I>
 *            type of elements to be processed.
 *
 */
public abstract class AbstractDCStage<I> extends AbstractStage { // IMPLEMENTS IDUPLICABLE (anderer Branch)

	// TODO arraylist!
	// BETTER private final I[] buffer but see next TODO (l38)
	private I leftBuffer = null;
	private I rightBuffer = null;

	private final DynamicConfigurationContext context;

	protected final InputPort<I> inputPort = this.createInputPort();
	protected final InputPort<I> leftInputPort = this.createInputPort();
	protected final InputPort<I> rightInputPort = this.createInputPort();

	protected final OutputPort<I> outputPort = this.createOutputPort();
	protected final OutputPort<I> leftOutputPort = this.createOutputPort();
	protected final OutputPort<I> rightOutputPort = this.createOutputPort();

	/**
	 * Divide and Conquer stages need the configuration context upon creation
	 *
	 */
	public AbstractDCStage(final DynamicConfigurationContext context) {
		if (null == context) {
			throw new IllegalArgumentException("Context may not be null.");
		}
		// TODO this.buffer = (I[]) new Object[size]; but can't figure out 'size'
		this.context = context;
		// connect to self instead of dummy pipe upon creation
		context.connectPorts(leftOutputPort, leftInputPort);
		context.connectPorts(rightOutputPort, rightInputPort);
	}

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	public final InputPort<I> getLeftInputPort() {
		return this.leftInputPort;
	}

	public final InputPort<I> getRightInputPort() {
		return this.rightInputPort;
	}

	public final OutputPort<I> getOutputPort() {
		return this.outputPort;
	}

	public final OutputPort<I> getleftOutputPort() {
		return this.leftOutputPort;
	}

	public final OutputPort<I> getrightOutputPort() {
		return this.rightOutputPort;
	}

	@Override
	protected final void executeStage() {

		// TODO STRUKTUR!
		final I element = this.getInputPort().receive();
		final I eLeft = this.getLeftInputPort().receive();
		final I eRight = this.getRightInputPort().receive();

		if (eLeft != null) {
			this.logger.debug("Left " + eLeft.toString());
			if (eRight != null) {
				this.logger.debug("Right " + eRight.toString());
				conquer(eLeft, eRight);
			} else {
				if (rightBuffer != null) {
					this.logger.debug("RightB " + rightBuffer.toString());
					conquer(eLeft, rightBuffer);
				} else {
					leftBuffer = eLeft;
				}
			}
		} else if (eRight != null) {
			if (leftBuffer != null) {
				this.logger.debug("LeftB " + leftBuffer.toString());
				conquer(leftBuffer, eRight);
			} else {
				rightBuffer = eRight;
			}
		} else if (element != null) {
			this.logger.debug("E " + element.toString());
			if (splitCondition(element)) {
				this.logger.debug("[DC]" + this.getId() + "_" + "passed splitcondition_" + element.toString());

				// SPLITCOUNT THREASHHOLD NUMTHREADS
				makeCopy(leftOutputPort, leftInputPort);
				makeCopy(rightOutputPort, rightInputPort);
				this.logger.debug("[DC]" + this.getId() + "_" + "DIVIDING_" + element.toString());
				divide(element);
			} else {
				this.logger.debug("[DC]" + this.getId() + "_" + "SOLVING_" + element.toString());
				solve(element);
			}
		} else {
			this.logger.debug("NO ELEMENT RECEIVED!");
			returnNoElement();
		}
	}

	/**
	 * A method to add a new copy (new instance) of this stage to the configuration, which should be executed in a own thread.
	 *
	 */
	private void makeCopy(final OutputPort<I> out, final InputPort<I> in) {
		final AbstractDCStage<I> newStage = debugCreateMethod();
		context.connectPorts(out, newStage.getInputPort());
		context.connectPorts(newStage.getOutputPort(), in);
		context.beginThread(newStage);
		context.sendSignals(out);
	}

	// BETTER Write the function code in this class instead
	protected abstract AbstractDCStage<I> debugCreateMethod();

	/**
	 * Method to divide the given input and send to the left and right output ports.
	 *
	 * @param element
	 *            An element to be split and further processed
	 */
	protected abstract void divide(final I element);

	/**
	 * Method to process the given input and send to the output port.
	 *
	 * @param element
	 *            An element to be processed
	 */
	protected abstract void solve(final I element);

	/**
	 * Method to join the given inputs together and send to the output port.
	 *
	 * @param eLeft
	 *            First half of the resulting element.
	 * @param eRight
	 *            Second half of the resulting element.
	 */
	protected abstract void conquer(final I eLeft, final I eRight);

	/**
	 * Determines whether or not to split the input problem by examining the given element
	 *
	 * @param element
	 *            The element whose properties determine the split condition
	 */
	protected abstract boolean splitCondition(final I element);

	// TODO get rid of this
	public DynamicConfigurationContext getContext() {
		// TODO Auto-generated method stub
		return this.context;
	}
}
