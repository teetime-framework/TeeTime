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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

/**
 * Represents a configuration of connected stages, which is needed to run a analysis.
 * Stages can be added by executing {@link #addThreadableStage(Stage)}.
 */
public abstract class AnalysisConfiguration {

	private final List<Stage> threadableStageJobs = new LinkedList<Stage>();
	private final IPipeFactory intraThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
	private final IPipeFactory interBoundedThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, true);
	private final IPipeFactory interUnboundedThreadFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

	/**
	 * Can be used by subclasses, to obtain pipe factories
	 */
	@Deprecated
	// TODO: set private
	protected static final PipeFactoryRegistry PIPE_FACTORY_REGISTRY = PipeFactoryRegistry.INSTANCE;

	List<Stage> getThreadableStageJobs() {
		return this.threadableStageJobs;
	}

	/**
	 * Execute this method, to add a stage to the configuration, which should be executed in a own thread.
	 *
	 * @param stage
	 *            A arbitrary stage, which will be added to the configuration and executed in a thread.
	 */
	protected void addThreadableStage(final Stage stage) {
		this.threadableStageJobs.add(stage);
	}

	protected <T> IPipe connectIntraThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return intraThreadFactory.create(sourcePort, targetPort);
	}

	protected <T> IPipe connectBoundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return interBoundedThreadFactory.create(sourcePort, targetPort);
	}

	protected <T> IPipe connectUnboundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return interUnboundedThreadFactory.create(sourcePort, targetPort);
	}

	protected <T> IPipe connectBoundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return interBoundedThreadFactory.create(sourcePort, targetPort, capacity);
	}

	protected <T> IPipe connectUnboundedInterThreads(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return interUnboundedThreadFactory.create(sourcePort, targetPort, capacity);
	}

}
