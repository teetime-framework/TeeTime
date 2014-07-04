package teetime.variant.methodcallWithPorts.framework.core;


public abstract class ProducerStage<I, O> extends AbstractStage<I, O> {

	public ProducerStage() {
		this.setReschedulable(true);
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
