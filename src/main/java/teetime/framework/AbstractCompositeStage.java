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

import teetime.framework.pipe.InstantiationPipe;

/**
 * Represents a minimal stage that composes several other stages.
 *
 * @since 2.0
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 */
public abstract class AbstractCompositeStage {

	/**
	 * Default capacity for pipes
	 */
	private static final int DEFAULT_CAPACITY = 4;

	// private final ConfigurationContext context;

	public AbstractCompositeStage() {
		// this.context = new ConfigurationContext(this);
	}

	// ConfigurationContext getContext() {
	// return context;
	// }

	/**
	 * Execute this method, to add a stage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary stage, which will be added to the configuration and executed in a thread.
	 */
	protected final void addThreadableStage(final Stage stage) {
		this.addThreadableStage(stage, stage.getId());
	}

	/**
	 * Execute this method, to add a stage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary stage, which will be added to the configuration and executed in a thread.
	 * @param threadName
	 *            A string which can be used for debugging.
	 */
	protected final void addThreadableStage(final Stage stage, final String threadName) {
		// context.addThreadableStage(stage, threadName);
		stage.setOwningThread(new Thread(threadName));
	}

	/**
	 * Connects two ports with a pipe with a default capacity of currently {@value #DEFAULT_CAPACITY}.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 */
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		connectPorts(sourcePort, targetPort, DEFAULT_CAPACITY);
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
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		// context.connectPorts(sourcePort, targetPort, capacity);
		connectPortsInternal(sourcePort, targetPort, capacity);
	}

	private final <T> void connectPortsInternal(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if (sourcePort.getOwningStage().getInputPorts().size() == 0) {
			// if (!threadService.getThreadableStages().containsKey(sourcePort.getOwningStage())) {
			if (sourcePort.getOwningStage().getOwningThread() == null) {
				addThreadableStage(sourcePort.getOwningStage(), sourcePort.getOwningStage().getId());
			}
		}

		// if (LOGGER.isWarnEnabled() && (sourcePort.getPipe() != null || targetPort.getPipe() != null)) {
		// LOGGER.warn("Overwriting existing pipe while connecting stages " +
		// sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		// }

		// addChildContext(sourcePort.getOwningStage());
		// addChildContext(targetPort.getOwningStage());

		new InstantiationPipe<T>(sourcePort, targetPort, capacity);
	}

}
