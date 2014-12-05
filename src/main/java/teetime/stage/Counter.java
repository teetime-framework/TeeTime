package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public class Counter<T> extends AbstractConsumerStage<T> {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private int numElementsPassed;

	@Override
	protected void execute(final T element) {
		this.numElementsPassed++;

		outputPort.send(element);
	}

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumElementsPassed() {
		return this.numElementsPassed;
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}
}
