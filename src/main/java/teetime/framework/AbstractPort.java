package teetime.framework;

import teetime.framework.pipe.IPipe;

public abstract class AbstractPort<T> {

	protected IPipe pipe;
	/**
	 * The type of this port.
	 * <p>
	 * <i>Used to validate the connection between two ports at runtime.</i>
	 * </p>
	 */
	protected Class<T> type;

	public IPipe getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe pipe) {
		this.pipe = pipe;
	}

	public Class<T> getType() {
		return this.type;
	}

	public void setType(final Class<T> type) {
		this.type = type;
	}
}
