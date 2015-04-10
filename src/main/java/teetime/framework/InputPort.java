/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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

public final class InputPort<T> extends AbstractPort<T> {

	private final Stage owningStage;

	InputPort(final Stage owningStage) {
		super();
		this.owningStage = owningStage;
	}

	/**
	 *
	 * @return the next element from the connected pipe
	 */
	@SuppressWarnings("unchecked")
	public T receive() {
		return (T) this.pipe.removeLast();
	}

	public Stage getOwningStage() {
		return this.owningStage;
	}

	public boolean isClosed() {
		return pipe.isClosed() && !pipe.hasMore();
	}

	public void waitForStartSignal() throws InterruptedException {
		pipe.waitForStartSignal();
	}

}
