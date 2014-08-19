package teetime.variant.methodcallWithPorts.framework.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.variant.methodcallWithPorts.framework.core.pipe.DummyPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;

public abstract class AbstractStage implements StageWithPort {

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected final Logger logger; // BETTER use SLF4J as interface and logback as impl

	private StageWithPort parentStage;

	private boolean reschedulable;

	private final List<InputPort<?>> inputPortList = new ArrayList<InputPort<?>>();
	private final List<OutputPort<?>> outputPortList = new ArrayList<OutputPort<?>>();

	/** A cached instance of <code>inputPortList</code> to avoid creating an iterator each time iterating it */
	protected InputPort<?>[] cachedInputPorts;
	/** A cached instance of <code>outputPortList</code> to avoid creating an iterator each time iterating it */
	protected OutputPort<?>[] cachedOutputPorts;

	public AbstractStage() {
		this.id = UUID.randomUUID().toString(); // the id should only be represented by a UUID, not additionally by the class name
		this.logger = LoggerFactory.getLogger(this.getClass().getName() + "(" + this.id + ")");
	}

	/**
	 * Sends the given <code>element</code> using the default output port
	 *
	 * @param element
	 * @return <code>true</code> iff the given element could be sent, <code>false</code> otherwise (then use a re-try strategy)
	 */
	protected final <O> boolean send(final OutputPort<O> outputPort, final O element) {
		if (!outputPort.send(element)) {
			return false;
		}

		StageWithPort next = outputPort.getCachedTargetStage();

		do {
			next.executeWithPorts(); // PERFORMANCE use the return value as indicator for re-schedulability instead
		} while (next.isReschedulable());

		return true;
	}

	@Override
	public void onStart() {
		this.cachedInputPorts = this.inputPortList.toArray(new InputPort<?>[0]);
		this.cachedOutputPorts = this.outputPortList.toArray(new OutputPort<?>[0]);

		this.connectUnconnectedOutputPorts();
	}

	@SuppressWarnings("unchecked")
	private void connectUnconnectedOutputPorts() {
		for (OutputPort<?> outputPort : this.cachedOutputPorts) {
			if (null == outputPort.getPipe()) { // if port is unconnected
				this.logger.warn("Unconnected output port: " + outputPort + ". Connecting with a dummy output port.");
				outputPort.setPipe(new DummyPipe());
			}
		}
	}

	protected void onFinished() {
		// empty default implementation
		this.onIsPipelineHead();
	}

	protected InputPort<?>[] getInputPorts() {
		return this.cachedInputPorts;
	}

	protected OutputPort<?>[] getOutputPorts() {
		return this.cachedOutputPorts;
	}

	@Override
	public StageWithPort getParentStage() {
		return this.parentStage;
	}

	@Override
	public void setParentStage(final StageWithPort parentStage, final int index) {
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

		for (OutputPort<?> outputPort : this.outputPortList) {
			outputPort.sendSignal(signal);
		}
	}

	protected <T> InputPort<T> createInputPort() {
		InputPort<T> inputPort = new InputPort<T>(this);
		// inputPort.setType(type); // TODO set type for input port
		this.inputPortList.add(inputPort);
		return inputPort;
	}

	protected <T> OutputPort<T> createOutputPort() {
		OutputPort<T> outputPort = new OutputPort<T>();
		// outputPort.setType(type); // TODO set type for output port
		this.outputPortList.add(outputPort);
		return outputPort;
	}

	public List<InvalidPortConnection> validateOutputPorts() {
		List<InvalidPortConnection> invalidOutputPortMessages = new LinkedList<InvalidPortConnection>();

		for (OutputPort<?> outputPort : this.getOutputPorts()) {
			IPipe<?> pipe = outputPort.getPipe();
			if (null != pipe) { // if output port is connected with another one
				Class<?> sourcePortType = outputPort.getType();
				Class<?> targetPortType = pipe.getTargetPort().getType();
				if (null == sourcePortType || !sourcePortType.equals(targetPortType)) {
					InvalidPortConnection invalidPortConnection = new InvalidPortConnection(outputPort, pipe.getTargetPort());
					invalidOutputPortMessages.add(invalidPortConnection);
				}
			}
		}

		return invalidOutputPortMessages;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.id;
	}

}
