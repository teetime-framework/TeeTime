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

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.InstantiationPipe;

/**
 * Represents a minimal stage that composes several other stages.
 * In order to work with this class, you need to extend from it and work from within the extending class.
 *
 * @since 2.0
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 */
public class CompositeStage {

	/**
	 * Default capacity for pipes
	 */
	protected static final int DEFAULT_PIPE_CAPACITY = 512;

	protected CompositeStage() {
		// prohibit direct instantiation of this class
	}

	/**
	 * Connects two ports with a pipe with a default capacity of currently {@value #DEFAULT_PIPE_CAPACITY}.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 */
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		connectPorts(sourcePort, targetPort, DEFAULT_PIPE_CAPACITY);
	}

	/**
	 * Connects to ports with a pipe of a certain capacity
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param capacity
	 *            the pipe is set to this capacity, if the value is greater than 0. If it is 0, than the pipe is unbounded, thus growing of the pipe is enabled.
	 * @param <T>
	 *            the type of elements to be sent
	 */
	protected <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if (sourcePort == null) {
			throw new IllegalArgumentException("1002 - SourcePort may not be null");
		}
		if (targetPort == null) {
			throw new IllegalArgumentException("1003 - TargetPort may not be null");
		}
		if (targetPort.getPipe() != null || sourcePort.getPipe() != DummyPipe.INSTANCE) {
			throw new IllegalStateException("1005 - Ports may not be reconnected");
		}
		new InstantiationPipe<T>(sourcePort, targetPort, capacity);
	}

}
