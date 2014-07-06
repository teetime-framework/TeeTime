package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;

public class InstanceCounter<T, C extends T> extends ConsumerStage<T, T> {

	private final Class<C> type;
	private int counter;

	public InstanceCounter(final Class<C> type) {
		this.type = type;
	}

	@Override
	protected void execute5(final T element) {
		if (this.type.isInstance(element)) {
			this.counter++;
		}

		this.send(element);
	}

	public int getCounter() {
		return this.counter;
	}

}
