package teetime.variant.methodcallWithPorts.framework.core;

public abstract class ProducerStage<I, O> extends AbstractStage<I, O> {

	public ProducerStage() {
		this.setReschedulable(true);
	}

	@Override
	public void executeWithPorts() {
		// if (this.logger.isDebugEnabled()) {
		// this.logger.debug("Executing stage...");
		// }

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
