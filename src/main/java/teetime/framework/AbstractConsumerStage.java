package teetime.framework;

public abstract class AbstractConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public void executeWithPorts() {
		final I element = this.getInputPort().receive();

		this.execute(element);
	}

	protected abstract void execute(I element);

}
