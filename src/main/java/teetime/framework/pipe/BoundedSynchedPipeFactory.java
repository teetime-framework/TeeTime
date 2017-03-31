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
package teetime.framework.pipe;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public final class BoundedSynchedPipeFactory implements IPipeFactory {

	public static final BoundedSynchedPipeFactory INSTANCE = new BoundedSynchedPipeFactory();

	private static final int DEFAULT_CAPACITY = 1024;

	private BoundedSynchedPipeFactory() {}

	/**
	 * Uses a default capacity of {@value #DEFAULT_CAPACITY}.
	 */
	@Override
	public <T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return newPipe(sourcePort, targetPort, DEFAULT_CAPACITY);
	}

	@Override
	public <T> IPipe<T> newPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new BoundedSynchedPipe<>(sourcePort, targetPort, capacity);
	}

}
