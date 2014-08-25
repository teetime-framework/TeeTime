package teetime.variant.methodcallWithPorts.framework.core.validation;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

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
