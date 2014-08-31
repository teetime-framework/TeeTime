package teetime.variant.methodcallWithPorts.framework.core;

/**
 * The <code>ProducerStage</code> produces at least one element at each execution.<br>
 *
 * @author Christian Wulf
 *
 * @param <O>
 *            the type of the default output port
 *
 */
public abstract class ProducerStage<O> extends AbstractStage implements HeadStage {

	protected final OutputPort<O> outputPort = this.createOutputPort();
	private boolean shouldTerminate;

	public final OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

	@Override
	public void executeWithPorts() {
		this.execute();
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	@Override
	public void terminate() {
		this.shouldTerminate = true;
	}

	@Override
	public boolean shouldBeTerminated() {
		return this.shouldTerminate;
	}

	protected abstract void execute();

}
