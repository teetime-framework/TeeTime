package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.NotEnoughInputException;
import teetime.framework.OutputPort;

public final class Relay<T> extends AbstractConsumerStage<T> {

	// private final InputPort<T> inputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	// private AbstractInterThreadPipe cachedCastedInputPipe;

	private static final NotEnoughInputException NOT_ENOUGH_INPUT_EXCEPTION = new NotEnoughInputException();

	@Override
	protected void execute(final T element) {
		if (null == element) {
			// if (this.cachedCastedInputPipe.getSignal() instanceof TerminatingSignal) {
			// this.terminate();
			// }
			// Thread.yield();
			// return;
			logger.trace("relay: returnNoElement");
			returnNoElement();
		}
		logger.trace("relay: " + element);
		outputPort.send(element);
	}

	private void returnNoElement() {
		throw NOT_ENOUGH_INPUT_EXCEPTION;
	}

	// @Override
	// public void onStarting() throws Exception {
	// super.onStarting();
	// this.cachedCastedInputPipe = (AbstractInterThreadPipe) this.inputPort.getPipe();
	// }

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
