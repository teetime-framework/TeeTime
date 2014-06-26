package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

public class CountingFilter<T> extends ConsumerStage<T, T> {

	private int numElementsPassed;

	@Override
	protected void execute5(final T element) {
		this.numElementsPassed++;
		this.send(element);
	}

	public int getNumElementsPassed() {
		return this.numElementsPassed;
	}

}
