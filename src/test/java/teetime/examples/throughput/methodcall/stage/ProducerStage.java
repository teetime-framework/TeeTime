package teetime.examples.throughput.methodcall.stage;

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
			this.getOutputPort().getPipe().close();
		}

		return outputElements;
	}

	@Override
	public void executeWithPorts() {
		this.execute5(null);

		// if (!this.getOutputPort().pipe.isEmpty()) {
		// super.executeWithPorts();
		// }
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

}
