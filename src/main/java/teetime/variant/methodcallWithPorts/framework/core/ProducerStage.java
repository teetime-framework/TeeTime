package teetime.variant.methodcallWithPorts.framework.core;

public abstract class ProducerStage<O> extends AbstractStage {

	protected final OutputPort<O> outputPort = this.createOutputPort();

	public final OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

	public ProducerStage() {
		this.setReschedulable(true);
	}

	@Override
	public void executeWithPorts() {
		this.execute();
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	protected abstract void execute();

}
