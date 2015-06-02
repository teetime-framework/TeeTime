package teetime.util;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class Connection<T> {

	private final OutputPort<? extends T> sourcePort;
	private final InputPort<T> targetPort;
	private final int capacity;

	public Connection(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		this(sourcePort, targetPort, 4);
	}

	public Connection(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public OutputPort<? extends T> getSourcePort() {
		return sourcePort;
	}

	public InputPort<T> getTargetPort() {
		return targetPort;
	}

}
