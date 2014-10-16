package teetime.stage;

import teetime.framework.ProducerStage;

public class IterableProducer<O extends Iterable<T>, T> extends ProducerStage<T> {

	private O iter = null;

	public IterableProducer(final O iter) {
		this.iter = iter;
	}

	@Override
	protected void execute() {
		for (T i : iter) {
			this.send(this.outputPort, i);
		}

	}

}
