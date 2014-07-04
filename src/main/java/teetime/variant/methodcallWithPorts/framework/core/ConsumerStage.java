package teetime.variant.methodcallWithPorts.framework.core;


public abstract class ConsumerStage<I, O> extends AbstractStage<I, O> {

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
