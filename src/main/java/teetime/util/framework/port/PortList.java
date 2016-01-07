/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.util.framework.port;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teetime.framework.AbstractPort;

public class PortList<T extends AbstractPort<?>> {

	private final List<T> openedPorts = new ArrayList<T>();

	// private final List<T> closedPorts = new ArrayList<T>();

	private final Set<PortRemovedListener<T>> portsRemovedListeners = new HashSet<PortRemovedListener<T>>();

	public List<T> getOpenedPorts() {
		return openedPorts;
	}

	// public List<T> getClosedPorts() {
	// return closedPorts;
	// }

	public boolean add(final T port) {
		return openedPorts.add(port);
	}

	public boolean remove(final T port) {
		boolean removed = openedPorts.remove(port); // BETTER remove by index for performance reasons
		firePortRemoved(port);
		if (!removed) {
			throw new IllegalStateException();
		}
		return removed;
	}

	public boolean close(final T port) {
		boolean removed = remove(port);
		// if (removed) {
		// boolean added = closedPorts.add(port);
		// if (added) {
		// return true;
		// }
		// openedPorts.add(port);
		// }
		return removed;
	}

	public int size() {
		return openedPorts.size();
	}

	private void firePortRemoved(final T removedPort) {
		for (PortRemovedListener<T> listener : portsRemovedListeners) {
			listener.onPortRemoved(removedPort);
		}
	}

	public void addPortRemovedListener(final PortRemovedListener<T> listener) {
		portsRemovedListeners.add(listener);
	}

}
