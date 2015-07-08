package teetime.stage.basic.distributor.dynamic;

import teetime.framework.DynamicOutputPort;

/**
 * Represents a container that eventually holds the output port that a {@link RemovePortActionDistributor} can use.
 *
 * @author Christian Wulf
 *
 * @param <T>
 */
class PortContainer<T> extends DynamicOutputPort<T> {

	private DynamicOutputPort<T> port;

	PortContainer() {
		super(null, null, -1);
	}

	@Override
	public int getIndex() {
		return port.getIndex();
	}

	public void setPort(final DynamicOutputPort<T> port) {
		this.port = port;
	}

}
