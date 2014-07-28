package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.AbstractStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Signal;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;

public class Relay<T> extends AbstractStage {

	private final InputPort<T> inputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	private SpScPipe<T> cachedCastedInputPipe;

	public Relay() {
		this.setReschedulable(true);
	}

	@Override
	public void executeWithPorts() {
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

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}
}
