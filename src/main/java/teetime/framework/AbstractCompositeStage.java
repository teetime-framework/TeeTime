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

	private final ConfigurationContext context;

<<<<<<< Upstream, based on origin/master
	public AbstractCompositeStage(final ConfigurationContext context) {
		if (null == context) {
			throw new IllegalArgumentException("Context may not be null.");
=======
	protected abstract Stage getFirstStage();

	protected final Collection<? extends Stage> getLastStages() {
		return lastStages;
	}

	@Override
	protected final void executeStage() {
		getFirstStage().executeStage();
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		getFirstStage().onSignal(signal, inputPort);
	}

	@Override
	protected final TerminationStrategy getTerminationStrategy() {
		return getFirstStage().getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		getFirstStage().terminate();
	}

	@Override
	protected final boolean shouldBeTerminated() {
		return getFirstStage().shouldBeTerminated();
	}

	@Override
	public final InputPort<?>[] getInputPorts() {
		return getFirstStage().getInputPorts();
	}

	@Override
	public OutputPort<?>[] getOutputPorts() {
		List<OutputPort<?>> outputPorts = new ArrayList<OutputPort<?>>();
		for (final Stage s : getLastStages()) {
			outputPorts.addAll(Arrays.asList(s.getOutputPorts()));
>>>>>>> 0840fe3 Test works now :)
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
