package teetime.util.framework.port;

import teetime.framework.AbstractPort;

public interface PortRemovedListener<T extends AbstractPort<?>> {

	void onPortRemoved(T removedPort);
}
