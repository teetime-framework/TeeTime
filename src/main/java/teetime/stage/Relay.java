package teetime.stage;

import teetime.framework.AbstractInterThreadPipe;
import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.signal.TerminatingSignal;

public final class Relay<T> extends AbstractProducerStage<T> {

	private final InputPort<T> inputPort = this.createInputPort();

	private AbstractInterThreadPipe cachedCastedInputPipe;

	@Override
	public void execute() {
		T element = this.inputPort.receive();
		if (null == element) {
			if (this.cachedCastedInputPipe.getSignal() instanceof TerminatingSignal) {
				this.terminate();
			}
			Thread.yield();
			return;
		}
		outputPort.send(element);
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
