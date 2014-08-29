package teetime.variant.methodcallWithPorts.stage;

import teetime.variant.methodcallWithPorts.framework.core.ProducerStage;

public class InitialElementProducer<T> extends ProducerStage<T> {

	private final T[] elements;

	public InitialElementProducer(final T... elements) {
		this.elements = elements;
	}

	@Override
	protected void execute() {
		for (T e : this.elements) {
			this.send(this.outputPort, e);
		}
		this.terminate();
	}

}
