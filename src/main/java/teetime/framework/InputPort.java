/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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

	InputPort(final Class<T> type, final Stage owningStage, final String portName) {
		super(type, owningStage, portName);
	}

	/**
	 *
	 * @return the next element from the connected pipe
	 */
	@SuppressWarnings("unchecked")
	public T receive() {
		return (T) this.pipe.removeLast();
	}

	public boolean isClosed() {
		return pipe.isClosed() && !pipe.hasMore();
	}

	public void waitForStartSignal() throws InterruptedException {
		pipe.waitForStartSignal();
	}

}
