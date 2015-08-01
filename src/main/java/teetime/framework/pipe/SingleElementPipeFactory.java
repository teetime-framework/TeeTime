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
package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class SingleElementPipeFactory implements IPipeFactory {

	@Override
	public <T> IPipe<T> create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return this.create(sourcePort, targetPort, 1);
	}

	/**
	 * Hint: The capacity for this pipe implementation is ignored.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public <T> IPipe<T> create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new SingleElementPipe<T>(sourcePort, targetPort);
	}

	@Override
	public boolean isGrowable() {
		return false;
	}

}
