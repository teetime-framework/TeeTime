package teetime.framework;

import teetime.framework.signal.InitializingSignal;
import teetime.framework.signal.StartingSignal;

public abstract class AbstractDCStage<I> extends AbstractStage {

	// private static final DynamicActuator DYNAMIC_ACTUATOR = new DynamicActuator();

	private final ConfigurationContext context;

	protected final InputPort<I> inputPort = this.createInputPort();
	protected final InputPort<I> leftInputPort = this.createInputPort();
	protected final InputPort<I> rightInputPort = this.createInputPort();

	protected final OutputPort<I> outputPort = this.createOutputPort();
	protected final OutputPort<I> leftOutputPort = this.createOutputPort();
	protected final OutputPort<I> rightOutputPort = this.createOutputPort();

	protected AbstractDCStage() {
		this.context = new ConfigurationContext();
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
		final I element = this.getInputPort().receive();
		final I eLeft = this.getLeftInputPort().receive();
		final I eRight = this.getRightInputPort().receive();

		if (null != element) {
			if (splitCondition(element)) {
				I[] elements = divide(element);
				AbstractDCStage<I>[] stages = null;
				stages = this.createCopies();
				// connect with copies
				// execute copy in new thread
				// send signals init start

				context.connectPorts(leftOutputPort, stages[0].getInputPort(), 1);
				context.connectPorts(rightOutputPort, stages[1].getInputPort(), 1);

				leftOutputPort.send(elements[0]);
				rightOutputPort.send(elements[1]);

				// Runnable runnable =
				// DYNAMIC_ACTUATOR.wrap(inputPort.getOwningStage());
				// Thread thread = new Thread(runnable);
				// thread.start();

				leftOutputPort.sendSignal(new InitializingSignal());
				leftOutputPort.sendSignal(new StartingSignal());
				rightOutputPort.sendSignal(new InitializingSignal());
				rightOutputPort.sendSignal(new StartingSignal());
			}
		} else if (eLeft != null && eRight != null) {
			outputPort.send(conquer(eLeft, eRight));
		} else {
			returnNoElement();
		}
	}

	protected abstract I conquer(I eLeft, I eRight);

	protected abstract boolean splitCondition(I element);

	protected abstract AbstractDCStage<I>[] createCopies(); // FIXME Should be in this class, need to find elegant way to write it generic

	protected abstract I[] divide(I element);
}
