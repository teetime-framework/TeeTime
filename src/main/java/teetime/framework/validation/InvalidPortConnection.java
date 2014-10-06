package teetime.framework.validation;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class InvalidPortConnection {

	private final OutputPort<?> sourcePort;
	private final InputPort<?> targetPort;

	public InvalidPortConnection(final OutputPort<?> sourcePort, final InputPort<?> targetPort) {
		super();
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
	}

	public OutputPort<?> getSourcePort() {
		return this.sourcePort;
	}

	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public String toString() {
		String sourcePortTypeName = (this.sourcePort.getType() == null) ? null : this.sourcePort.getType().getName();
		String targetPortTypeName = (this.targetPort.getType() == null) ? null : this.targetPort.getType().getName();
		return sourcePortTypeName + " != " + targetPortTypeName;
	}

}
