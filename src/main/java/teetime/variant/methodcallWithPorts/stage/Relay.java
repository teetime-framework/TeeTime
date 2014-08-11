package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.ProducerStage;
import teetime.variant.methodcallWithPorts.framework.core.Signal;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;

public class Relay<T> extends ProducerStage<T> {

	private final InputPort<T> inputPort = this.createInputPort();

	private SpScPipe<T> cachedCastedInputPipe;

	@Override
	public void execute() {
		T element = this.inputPort.receive();
		if (null == element) {
			// if (this.getInputPort().getPipe().isClosed()) {
			if (this.cachedCastedInputPipe.getSignal() == Signal.FINISHED) {
				this.setReschedulable(false);
				assert 0 == this.inputPort.getPipe().size();
			}
			Thread.yield();
			return;
		}
		this.send(this.outputPort, element);
	}

	@Override
	public void onStart() {
		this.cachedCastedInputPipe = (SpScPipe<T>) this.inputPort.getPipe();
		super.onStart();
	}

	@Override
	public void onIsPipelineHead() {
		// if (this.getInputPort().getPipe().isClosed()) {
		// this.setReschedulable(false);
		// }
	}

	public InputPort<T> getInputPort() {
		return this.inputPort;
	}
}
