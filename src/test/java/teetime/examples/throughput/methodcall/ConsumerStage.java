package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;

public abstract class ConsumerStage<I, O> extends AbstractStage<I, O> {

	@Override
	public CommittableQueue<O> execute2(final CommittableQueue<I> elements) {
		boolean inputIsEmpty = elements.isEmpty();
		if (inputIsEmpty) {
			this.disable();
			return this.outputElements;
		}

		return super.execute2(elements);
	}

}
