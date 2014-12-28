package teetime.framework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

public abstract class AbstractStage extends Stage {

	private static final IPipe DUMMY_PORT = new DummyPipe();

	private final List<InputPort<?>> inputPortList = new ArrayList<InputPort<?>>();
	private final List<OutputPort<?>> outputPortList = new ArrayList<OutputPort<?>>();

	/** A cached instance of <code>inputPortList</code> to avoid creating an iterator each time iterating it */
	protected InputPort<?>[] cachedInputPorts = new InputPort[0];
	/** A cached instance of <code>outputPortList</code> to avoid creating an iterator each time iterating it */
	protected OutputPort<?>[] cachedOutputPorts;

	private final Set<ISignal> triggeredSignals = new HashSet<ISignal>();
	// BETTER aggregate both states in an enum
	private boolean shouldTerminate;
	private boolean started;

	/**
	 * @return the stage's input ports
	 */
	@Override
	public InputPort<?>[] getInputPorts() {
		// return this.cachedInputPorts;
		return inputPortList.toArray(new InputPort<?>[0]); // FIXME remove work-around
	}

	/**
	 * @return the stage's output ports
	 */
	protected OutputPort<?>[] getOutputPorts() {
		return this.cachedOutputPorts;
	}

	/**
	 * May not be invoked outside of IPipe implementations
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		if (!this.signalAlreadyReceived(signal, inputPort)) {
			signal.trigger(this);

			for (OutputPort<?> outputPort : this.outputPortList) {
				outputPort.sendSignal(signal);
			}
		}
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	/**
	 * @param signal
	 *            arriving signal
	 * @param inputPort
	 *            which received the signal
	 * @return <code>true</code> if this stage has already received the given <code>signal</code>, <code>false</code> otherwise
	 */
	protected boolean signalAlreadyReceived(final ISignal signal, final InputPort<?> inputPort) {
		if (this.triggeredSignals.contains(signal)) {
			this.logger.trace("Got signal: " + signal + " again from input port: " + inputPort);
			return true;
		} else {
			this.logger.trace("Got signal: " + signal + " from input port: " + inputPort);
			this.triggeredSignals.add(signal);
			return false;
		}
	}

	public void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		this.validateOutputPorts(invalidPortConnections);
	}

	public void onStarting() throws Exception {
		this.owningThread = Thread.currentThread();
		this.cachedInputPorts = this.inputPortList.toArray(new InputPort<?>[0]);
		this.cachedOutputPorts = this.outputPortList.toArray(new OutputPort<?>[0]);

		this.connectUnconnectedOutputPorts();
		started = true;
		logger.debug("Started.");
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private void connectUnconnectedOutputPorts() {
		for (OutputPort<?> outputPort : this.cachedOutputPorts) {
			if (null == outputPort.getPipe()) { // if port is unconnected
				this.logger.warn("Unconnected output port: " + outputPort + ". Connecting with a dummy output port.");
				outputPort.setPipe(DUMMY_PORT);
			}
		}
	}

	public void onTerminating() throws Exception {
		this.terminate();
	}

	/**
	 * Creates and adds an InputPort to the stage
	 *
	 * @return Newly added InputPort
	 */
	protected <T> InputPort<T> createInputPort() {
		final InputPort<T> inputPort = new InputPort<T>(this);
		// inputPort.setType(portType);
		this.inputPortList.add(inputPort);
		return inputPort;
	}

	/**
	 * Creates and adds an OutputPort to the stage
	 *
	 * @return Newly added OutputPort
	 */
	protected <T> OutputPort<T> createOutputPort() {
		final OutputPort<T> outputPort = new OutputPort<T>();
		// outputPort.setType(portType);
		this.outputPortList.add(outputPort);
		return outputPort;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		// for (OutputPort<?> outputPort : this.getOutputPorts()) {
		for (OutputPort<?> outputPort : this.outputPortList) {
			final IPipe pipe = outputPort.getPipe();
			if (null != pipe) { // if output port is connected with another one
				final Class<?> sourcePortType = outputPort.getType();
				final Class<?> targetPortType = pipe.getTargetPort().getType();
				if (null == sourcePortType || !sourcePortType.equals(targetPortType)) {
					final InvalidPortConnection invalidPortConnection = new InvalidPortConnection(outputPort, pipe.getTargetPort());
					invalidPortConnections.add(invalidPortConnection);
				}
			}
		}
	}

	@Override
	public void terminate() {
		this.shouldTerminate = true;
	}

	@Override
	public boolean shouldBeTerminated() {
		return this.shouldTerminate;
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return TerminationStrategy.BY_SIGNAL;
	}

}
