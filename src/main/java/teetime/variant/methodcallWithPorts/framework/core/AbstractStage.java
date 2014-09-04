package teetime.variant.methodcallWithPorts.framework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.variant.methodcallWithPorts.framework.core.pipe.DummyPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;
import teetime.variant.methodcallWithPorts.framework.core.signal.Signal;
import teetime.variant.methodcallWithPorts.framework.core.validation.InvalidPortConnection;

public abstract class AbstractStage implements StageWithPort {

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	protected final Logger logger;

	private StageWithPort parentStage;

	private final List<InputPort<?>> inputPortList = new ArrayList<InputPort<?>>();
	private final List<OutputPort<?>> outputPortList = new ArrayList<OutputPort<?>>();

	/** A cached instance of <code>inputPortList</code> to avoid creating an iterator each time iterating it */
	protected InputPort<?>[] cachedInputPorts;
	/** A cached instance of <code>outputPortList</code> to avoid creating an iterator each time iterating it */
	protected OutputPort<?>[] cachedOutputPorts;

	private final Map<Signal, Void> visited = new HashMap<Signal, Void>();

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

		outputPort.reportNewElement();

		return true;
		// return outputPort.send(element);
	}

	private void connectUnconnectedOutputPorts() {
		for (OutputPort<?> outputPort : this.cachedOutputPorts) {
			if (null == outputPort.getPipe()) { // if port is unconnected
				this.logger.warn("Unconnected output port: " + outputPort + ". Connecting with a dummy output port.");
				outputPort.setPipe(new DummyPipe());
			}
		}
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
	public String getId() {
		return this.id;
	}

	/**
	 * May not be invoked outside of IPipe implementations
	 */
	@Override
	public void onSignal(final Signal signal, final InputPort<?> inputPort) {
		if (!this.alreadyVisited(signal, inputPort)) {
			signal.trigger(this);

			for (OutputPort<?> outputPort : this.outputPortList) {
				outputPort.sendSignal(signal);
			}
		}
	}

	protected boolean alreadyVisited(final Signal signal, final InputPort<?> inputPort) {
		if (this.visited.containsKey(signal)) {
			this.logger.trace("Got signal: " + signal + " again from input port: " + inputPort);
			return true;
		} else {
			this.logger.trace("Got signal: " + signal + " from input port: " + inputPort);
			this.visited.put(signal, null);
			return false;
		}
	}

	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		this.validateOutputPorts(invalidPortConnections);
	}

	public void onStarting() {
		this.cachedInputPorts = this.inputPortList.toArray(new InputPort<?>[0]);
		this.cachedOutputPorts = this.outputPortList.toArray(new OutputPort<?>[0]);

		this.connectUnconnectedOutputPorts();
	}

	public void onTerminating() {
		// empty default implementation
		this.onIsPipelineHead();
	}

	protected <T> InputPort<T> createInputPort() {
		InputPort<T> inputPort = new InputPort<T>(this);
		// inputPort.setType(portType);
		this.inputPortList.add(inputPort);
		return inputPort;
	}

	protected <T> OutputPort<T> createOutputPort() {
		OutputPort<T> outputPort = new OutputPort<T>();
		// outputPort.setType(portType);
		this.outputPortList.add(outputPort);
		return outputPort;
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (OutputPort<?> outputPort : this.getOutputPorts()) {
			IPipe pipe = outputPort.getPipe();
			if (null != pipe) { // if output port is connected with another one
				Class<?> sourcePortType = outputPort.getType();
				Class<?> targetPortType = pipe.getTargetPort().getType();
				if (null == sourcePortType || !sourcePortType.equals(targetPortType)) {
					InvalidPortConnection invalidPortConnection = new InvalidPortConnection(outputPort, pipe.getTargetPort());
					invalidPortConnections.add(invalidPortConnection);
				}
			}
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.id;
	}

}
