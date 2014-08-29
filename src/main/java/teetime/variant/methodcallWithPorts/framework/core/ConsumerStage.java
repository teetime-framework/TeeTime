package teetime.variant.methodcallWithPorts.framework.core;

public abstract class ConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public void executeWithPorts() {
		I element = this.getInputPort().receive();

		this.execute(element);
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	protected abstract void execute(I element);

}
