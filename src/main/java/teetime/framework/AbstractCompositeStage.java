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

<<<<<<< Upstream, based on origin/master
	private final ConfigurationContext context;
=======
	protected final Set<Stage> containingStages = new HashSet<Stage>();
	protected final Set<Stage> lastStages = new HashSet<Stage>();
>>>>>>> c88f7bf Checks added to TaskFarmStage

	public AbstractCompositeStage(final ConfigurationContext context) {
		if (null == context) {
			throw new IllegalArgumentException("Context may not be null.");
		}
		this.context = context;
	}

	protected ConfigurationContext getContext() {
		return context;
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
		context.addThreadableStage(stage, threadName);
	}

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
		context.connectPorts(sourcePort, targetPort, DEFAULT_CAPACITY);
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
		context.connectPorts(sourcePort, targetPort, capacity);
	}

}
