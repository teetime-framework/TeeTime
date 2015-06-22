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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

/**
 * Represents a configuration of connected stages, which is needed to run a analysis.
 * Stages can be added by executing {@link #addThreadableStage(Stage)}.
 */
public abstract class ConfigurationContext extends Configuration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationContext.class);

	private final Set<Stage> threadableStages = new HashSet<Stage>();

	@SuppressWarnings("deprecation")
	private static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;

	/**
	 * Can be used by subclasses, to connect stages
	 */
	private final static IPipeFactory intraThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
	/**
	 * Can be used by subclasses, to connect stages
	 */
	private final static IPipeFactory interBoundedThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
	/**
	 * Can be used by subclasses, to connect stages
	 */
	private final static IPipeFactory interUnboundedThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, true);

	Set<Stage> getThreadableStages() {
		return this.threadableStages;
	}

	/**
	 * Execute this method, to add a stage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary stage, which will be added to the configuration and executed in a thread.
	 */
	@Override
	protected final void addThreadableStage(final Stage stage) {
		if (!this.threadableStages.add(stage)) {
			LOGGER.warn("Stage " + stage.getId() + " was already marked as threadable stage.");
		}
	}

	/**
	 * Connects two stages with a pipe within the same thread.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 * @return
	 *         the pipe instance which connects the two given stages
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectIntraThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return intraThreadFactory.create(sourcePort, targetPort);
	}

	/**
	 * Connects two stages with a bounded pipe within two separate threads.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 * @return
	 *         the pipe instance which connects the two given stages
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectBoundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return interBoundedThreadFactory.create(sourcePort, targetPort);
	}

	/**
	 * Connects two stages with a unbounded pipe within two separate threads.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 * @return
	 *         the pipe instance which connects the two given stages
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectUnboundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return interUnboundedThreadFactory.create(sourcePort, targetPort);
	}

	/**
	 * Connects two stages with a bounded pipe within two separate threads.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param capacity
	 *            capacity of the underlying queue
	 * @param <T>
	 *            the type of elements to be sent
	 * @return
	 *         the pipe instance which connects the two given stages
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectBoundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return interBoundedThreadFactory.create(sourcePort, targetPort, capacity);
	}

	/**
	 * Connects two stages with a unbounded pipe within two separate threads.
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param capacity
	 *            capacity of the underlying queue
	 * @param <T>
	 *            the type of elements to be sent
	 * @return
	 *         the pipe instance which connects the two given stages
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectUnboundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return interUnboundedThreadFactory.create(sourcePort, targetPort, capacity);
	}

	/**
	 * Connects two ports with a pipe with a default capacity of currently 4
	 *
	 * @param sourcePort
	 *            {@link OutputPort} of the sending stage
	 * @param targetPort
	 *            {@link InputPort} of the sending stage
	 * @param <T>
	 *            the type of elements to be sent
	 */
	@Override
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		connectPorts(sourcePort, targetPort, 4);
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
	@Override
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if (sourcePort.getOwningStage().getInputPorts().length == 0 && !threadableStages.contains(sourcePort.getOwningStage())) {
			addThreadableStage(sourcePort.getOwningStage());
		}
		if (sourcePort.getPipe() != null || targetPort.getPipe() != null) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		new InstantiationPipe(sourcePort, targetPort, capacity);
	}

}
