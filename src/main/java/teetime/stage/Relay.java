package teetime.stage;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.NotEnoughInputException;

public final class Relay<T> extends AbstractProducerStage<T> {

	private final InputPort<T> inputPort = this.createInputPort();

	private AbstractInterThreadPipe cachedCastedInputPipe;

	private static final NotEnoughInputException NOT_ENOUGH_INPUT_EXCEPTION = new NotEnoughInputException();

	@Override
	public void execute() {
		T element = this.inputPort.receive();
		if (null == element) {
			// if (this.cachedCastedInputPipe.getSignal() instanceof TerminatingSignal) {
			// this.terminate();
			// }
			// Thread.yield();
			// return;
			returnNoElement();
		}
		outputPort.send(element);
	}

	private void returnNoElement() {
		throw NOT_ENOUGH_INPUT_EXCEPTION;
	}

	@Override
	public void onStarting() throws Exception {
		super.onStarting();
		this.cachedCastedInputPipe = (AbstractInterThreadPipe) this.inputPort.getPipe();
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}
}
