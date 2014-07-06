package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

public class Counter<T> extends ConsumerStage<T, T> {

	private int numElementsPassed;

	@Override
	protected void execute5(final T element) {
		this.numElementsPassed++;
		// this.logger.info("count: " + this.numElementsPassed);
		this.send(element);
	}

	// BETTER find a solution w/o any thread-safe code in this stage
	public synchronized int getNumElementsPassed() {
		return this.numElementsPassed;
	}

}
