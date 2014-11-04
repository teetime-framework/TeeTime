package teetime.stage;

import teetime.framework.InputPort;
import teetime.framework.ProducerStage;
import teetime.framework.pipe.InterThreadPipe;
import teetime.framework.signal.TerminatingSignal;

public class Relay<T> extends ProducerStage<T> {

	private final InputPort<T> inputPort = this.createInputPort();

	private InterThreadPipe cachedCastedInputPipe;

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
		this.send(this.outputPort, element);
	}

	@Override
	public void onStarting() throws Exception {
		super.onStarting();
		this.cachedCastedInputPipe = (InterThreadPipe) this.inputPort.getPipe();
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}
}
