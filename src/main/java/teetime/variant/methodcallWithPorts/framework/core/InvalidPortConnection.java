package teetime.variant.methodcallWithPorts.framework.core;

public class InvalidPortConnection {

	private final OutputPort<?> sourcePort;
	private final InputPort<?> inputPort;

	public InvalidPortConnection(final OutputPort<?> sourcePort, final InputPort<?> inputPort) {
		super();
		this.sourcePort = sourcePort;
		this.inputPort = inputPort;
	}

	public OutputPort<?> getSourcePort() {
		return sourcePort;
	}

	public InputPort<?> getInputPort() {
		return inputPort;
	}

}
