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
public abstract class AnalysisConfiguration {

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
	protected final void addThreadableStage(final Stage stage) {
		this.threadableStages.add(stage);
	}

	/**
	 * Execute this method, to add a CompositeStage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary CompositeStage, which will be added to the configuration and executed in a thread.
	 */
	protected final void addThreadableStage(final AbstractCompositeStage stage) {
		this.threadableStages.add(stage.getFirstStage());
		for (Stage threadableStage : stage.getThreadableStages()) {
			this.addThreadableStage(threadableStage);
		}
	}

	/**
	 * Connects two stages with a pipe within the same thread.
	 *
	 * @param sourcePort
	 * @param targetPort
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
	 * @param targetPort
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
	 * @param targetPort
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
	 * @param targetPort
	 * @param capacity
	 *            capacity of the underlying queue
	 * @return
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
	 * @param targetPort
	 * @param capacity
	 *            capacity of the underlying queue
	 * @return
	 *
	 * @deprecated since 1.2. Use {@link #connectPorts(OutputPort, InputPort)} instead.
	 */
	@Deprecated
	protected static <T> IPipe connectUnboundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return interUnboundedThreadFactory.create(sourcePort, targetPort, capacity);
	}

	/**
	 * Connects two ports with a pipe.
	 *
	 * @param sourcePort
	 *            port from the sending stage
	 * @param targetPort
	 *            port from the receiving stage
	 */
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		connectPorts(sourcePort, targetPort, 4);
	}

	/**
	 * Connects to ports with a pipe of a certain capacity
	 *
	 * @param sourcePort
	 *            port from the sending stage
	 * @param targetPort
	 *            port from the receiving stage
	 * @param capacity
	 *            the pipe is set to this capacity, if the value is greater than 0. If it is 0, than the pipe is unbounded, thus growing of the pipe is enabled.
	 */
	protected final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		new InstantiationPipe(sourcePort, targetPort, capacity);
	}

}
