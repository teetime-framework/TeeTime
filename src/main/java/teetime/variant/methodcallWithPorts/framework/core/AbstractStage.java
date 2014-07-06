package teetime.variant.methodcallWithPorts.framework.core;

import java.util.UUID;

import teetime.util.list.CommittableQueue;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

public abstract class AbstractStage<I, O> implements StageWithPort<I, O> {

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected final Log logger; // BETTER use SLF4J as interface and logback as impl

	private final InputPort<I> inputPort = new InputPort<I>(this);
	private final OutputPort<O> outputPort = new OutputPort<O>();

	private StageWithPort<?, ?> parentStage;

	private boolean reschedulable;

	public AbstractStage() {
		this.id = UUID.randomUUID().toString(); // the id should only be represented by a UUID, not additionally by the class name
		this.logger = LogFactory.getLog(this.getClass().getName() + "(" + this.id + ")");
	}

	@Override
	public InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

	protected void execute4(final CommittableQueue<I> elements) {
		throw new IllegalStateException(); // default implementation
	}

	protected abstract void execute5(I element);

	/**
	 * Sends the <code>element</code> using the default output port
	 * 
	 * @param element
	 */
	protected final void send(final O element) {
		this.send(this.getOutputPort(), element);
	}

	protected final void send(final OutputPort<O> outputPort, final O element) {
		outputPort.send(element);

		// StageWithPort<?, ?> next = outputPort.getPipe().getTargetPort().getOwningStage();
		StageWithPort<?, ?> next = outputPort.getCachedTargetStage();

		do {
			next.executeWithPorts(); // PERFORMANCE use the return value as indicator for re-schedulability instead
		} while (next.isReschedulable());
	}

	// public void disable() {
	// this.schedulingInformation.setActive(false);
	// this.fireOnDisable();
	// }

	// private void fireOnDisable() {
	// if (this.listener != null) {
	// this.listener.onDisable(this, this.index);
	// }
	// }

	@Override
	public void onStart() {
		// empty default implementation
	}

	@Override
	public StageWithPort<?, ?> getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final StageWithPort<?, ?> parentStage, final int index) {
		this.parentStage = parentStage;
	}

	@Override
	public boolean isReschedulable() {
		return this.reschedulable;
	}

	public void setReschedulable(final boolean reschedulable) {
		this.reschedulable = reschedulable;
	}

	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * May not be invoked outside of IPipe implementations
	 */
	@Override
	public void onSignal(final Signal signal, final InputPort<?> inputPort) {
		this.logger.debug("Got signal: " + signal + " from input port: " + inputPort);

		switch (signal) {
		case FINISHED:
			this.onFinished();
			break;
		default:
			this.logger.warn("Aborted sending signal " + signal + ". Reason: Unknown signal.");
			break;
		}

		this.outputPort.sendSignal(signal);
	}

	protected void onFinished() {
		// empty default implementation
		this.onIsPipelineHead();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.id;
	}

}
