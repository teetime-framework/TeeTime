package teetime.variant.methodcallWithPorts.examples.traceReconstructionWithThreads;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;

public class SysOutFilter<T> extends ConsumerStage<T> {

	private final InputPort<Long> triggerInputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	private final IPipe pipe;

	public SysOutFilter(final IPipe pipe) {
		this.pipe = pipe;
	}

	@Override
	protected void execute(final T element) {
		Long timestamp = this.triggerInputPort.receive();
		if (timestamp != null) {
			// this.logger.info("pipe.size: " + this.pipe.size());
			System.out.println("pipe.size: " + this.pipe.size());
		}
		this.send(this.outputPort, element);
	}

	public InputPort<Long> getTriggerInputPort() {
		return this.triggerInputPort;
	}

}
