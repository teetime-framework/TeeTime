/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.basic.distributor.dynamic;

import teetime.framework.OutputPort;

/**
 * Represents a container that eventually holds the output port that a
 * {@link RemovePortActionDistributor} can use.
 *
 * @author Christian Wulf
 *
 * @param <T>
 */
final class PortContainer<T> {

	private OutputPort<T> port;

	PortContainer() {
		// empty constructor
	}

	public void setPort(final OutputPort<T> port) {
		this.port = port;
	}

	public OutputPort<T> getPort() {
		return port;
	}

}
