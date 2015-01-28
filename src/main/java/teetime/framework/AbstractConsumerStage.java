package teetime.framework;

import teetime.framework.exceptionHandling.StageException;
import teetime.framework.idle.IdleStrategy;
import teetime.framework.idle.YieldStrategy;

public abstract class AbstractConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	private IdleStrategy idleStrategy = new YieldStrategy(); // FIXME remove this word-around

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public final void executeWithPorts() {
		final I element = this.getInputPort().receive();
		if (null == element) {
			returnNoElement();
		}

		try {
			this.execute(element);
		} catch (Exception e) {
			throw new StageException(e, this);
		}
	}

	protected abstract void execute(I element);

	public IdleStrategy getIdleStrategy() {
		return idleStrategy;
	}

	public void setIdleStrategy(final IdleStrategy idleStrategy) {
		this.idleStrategy = idleStrategy;
	}
}
