package teetime.framework;

import teetime.framework.idle.IdleStrategy;

public abstract class AbstractConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	private IdleStrategy idleStrategy; // FIXME remove this word-around

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public void executeWithPorts() {
		final I element = this.getInputPort().receive();

		this.execute(element);
	}

	protected abstract void execute(I element);

	public IdleStrategy getIdleStrategy() {
		return idleStrategy;
	}

	public void setIdleStrategy(final IdleStrategy idleStrategy) {
		this.idleStrategy = idleStrategy;
	}
}
