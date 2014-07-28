package teetime.variant.methodcallWithPorts.framework.core;

public abstract class ConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public void executeWithPorts() {
		I element = this.inputPort.receive();

		boolean isReschedulable = this.determineReschedulability();
		this.setReschedulable(isReschedulable);

		this.execute(element);
	}

	@Override
	public void onIsPipelineHead() {
		// do nothing
	}

	/**
	 * 
	 * @return <code>true</code> iff this stage makes progress when it is re-executed by the scheduler, otherwise <code>false</code>.<br>
	 *         For example, many stages are re-schedulable if at least one of their input ports are not empty.
	 */
	protected boolean determineReschedulability() {
		return this.inputPort.getPipe().size() > 0;
	}

	protected abstract void execute(I element);

}
