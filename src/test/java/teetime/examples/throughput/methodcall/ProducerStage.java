package teetime.examples.throughput.methodcall;

import teetime.util.list.CommittableQueue;

public abstract class ProducerStage<I, O> extends AbstractStage<I, O> {

	public ProducerStage() {
		this.setReschedulable(true);
	}

	@Override
	public CommittableQueue<O> execute2(final CommittableQueue<I> elements) {
		CommittableQueue<O> outputElements = super.execute2(elements);

		boolean outputIsEmpty = outputElements.isEmpty();
		if (outputIsEmpty) {
			this.disable();
		}

		return outputElements;
	}

	@Override
	public void executeWithPorts() {
		this.execute5(null);
	}

}
