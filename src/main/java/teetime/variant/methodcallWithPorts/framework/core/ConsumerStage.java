package teetime.variant.methodcallWithPorts.framework.core;

public abstract class ConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public void executeWithPorts() {
		I element = this.getInputPort().receive();

		boolean isReschedulable = this.determineReschedulability();
		this.setReschedulable(isReschedulable);

		this.execute(element);
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	protected boolean determineReschedulability() {
		return this.getInputPort().getPipe().size() > 0;
	}

	protected abstract void execute(I element);

}
