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
package teetime.framework;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of elements to be sent
 *
 * @since 1.0
 */
public class InputPort<T> extends AbstractPort<T> {

	InputPort(final Class<T> type, final AbstractStage owningStage, final String portName) {
		super(type, owningStage, portName);
	}

	/**
	 * For testing purposes only.
	 */
	public InputPort() {
		super(null, null, null);
	}

	/**
	 *
	 * @return the next element from the connected pipe
	 */
	@SuppressWarnings("unchecked")
	public T receive() {
		Object element = this.pipe.removeLast();
		if (TERMINATE_ELEMENT == element) {
			pipe.close();// TODO remove volatile from isClosed
			int size = pipe.size();
			if (size > 0) {
				throw new IllegalStateException("Pipe " + pipe + " should be empty, but has a size of " + size);
			}
			AbstractStage owningStage = getOwningStage();
			int numOpenedInputPorts = owningStage.decNumOpenedInputPorts();
			owningStage.logger.trace("numOpenedInputPorts (dec): {}", numOpenedInputPorts);
			if (numOpenedInputPorts == 0) {
				owningStage.terminateStageByFramework();
			}
			return null; // NOPMD (two returns)
		}
		return (T) element;
	}

	public boolean isClosed() { // FIXME remove: only used by divide and conquer
		return pipe.isClosed() && !pipe.hasMore();
	}

	public void waitForStartSignal() throws InterruptedException {
		pipe.waitForStartSignal();
	}

}
