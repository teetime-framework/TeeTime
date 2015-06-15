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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourcePort == null) ? 0 : sourcePort.hashCode());
		result = prime * result + ((targetPort == null) ? 0 : targetPort.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Connection<?> other = (Connection<?>) obj;
		if (sourcePort == null) {
			if (other.sourcePort != null) {
				return false;
			}
		} else if (!sourcePort.equals(other.sourcePort)) {
			return false;
		}
		if (targetPort == null) {
			if (other.targetPort != null) {
				return false;
			}
		} else if (!targetPort.equals(other.targetPort)) {
			return false;
		}
		return true;
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
