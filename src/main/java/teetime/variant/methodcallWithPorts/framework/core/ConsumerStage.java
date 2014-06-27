package teetime.variant.methodcallWithPorts.framework.core;

import teetime.util.list.CommittableQueue;

public abstract class ConsumerStage<I, O> extends AbstractStage<I, O> {

	@Override
	public CommittableQueue<O> execute2(final CommittableQueue<I> elements) {
		// the following code block does not harm the performance
		// boolean inputIsEmpty = elements.isEmpty();
		// if (inputIsEmpty) {
		// this.disable();
		// return this.outputElements;
		// }

		CommittableQueue<O> output = super.execute2(elements);
		this.setReschedulable(!elements.isEmpty()); // costs ~1200 ns on chw-work (not reproducible)
		return output;
	}

	@Override
	public void executeWithPorts() {
		// if (this.logger.isDebugEnabled()) {
		// this.logger.debug("Executing stage...");
		// }

		I element = this.getInputPort().receive();

		this.setReschedulable(this.getInputPort().getPipe().size() > 0);

		this.execute5(element);
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

}
