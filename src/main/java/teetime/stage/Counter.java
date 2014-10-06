package teetime.stage;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

public class Counter<T> extends ConsumerStage<T> {

	private final OutputPort<T> outputPort = this.createOutputPort();

	private int numElementsPassed;

	@Override
	protected void execute(final T element) {
		this.numElementsPassed++;
		// this.logger.debug("count: " + this.numElementsPassed);
		this.send(this.outputPort, element);
	}

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumElementsPassed() {
		return this.numElementsPassed;
	}

	public OutputPort<T> getOutputPort() {
		return this.outputPort;
	}
}
