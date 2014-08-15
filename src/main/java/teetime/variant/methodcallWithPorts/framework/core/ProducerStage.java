package teetime.variant.methodcallWithPorts.framework.core;

/**
 * The <code>ProducerStage</code> produces at least one element at each execution.<br>
 *
 * @reschedulability
 *                   This stage is executed as long as its execute() method decided to do so.<br>
 *                   Refer to {@link AbstractStage#isReschedulable} for more information.
 *
 * @author Christian Wulf
 *
 * @param <O>
 *            the type of the default output port
 *
 */
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
