package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.ProducerStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.framework.core.signal.TerminatingSignal;

public class Relay<T> extends ProducerStage<T> {

	private final InputPort<T> inputPort = this.createInputPort();

	private SpScPipe<T> cachedCastedInputPipe;

	@Override
	public void execute() {
		T element = this.inputPort.receive();
		if (null == element) {
			if (this.cachedCastedInputPipe.getSignal() instanceof TerminatingSignal) {
				this.setReschedulable(false);
				assert 0 == this.inputPort.getPipe().size();
			}
			Thread.yield();
			return;
		}
		this.send(this.outputPort, element);
	}

	@Override
	public void onStarting() {
		this.cachedCastedInputPipe = (SpScPipe<T>) this.inputPort.getPipe();
		super.onStarting();
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}
}
