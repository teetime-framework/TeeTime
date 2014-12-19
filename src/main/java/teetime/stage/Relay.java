package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class Relay<T> extends AbstractConsumerStage<T> {

	// private final InputPort<T> inputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	// private AbstractInterThreadPipe cachedCastedInputPipe;

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
		outputPort.send(element);
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
