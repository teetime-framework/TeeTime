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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.InstantiationPipe;

/**
 * Represents a context that is used by a configuration and composite stages to connect ports, for example.
 * Stages can be added by executing {@link #addThreadableStage(Stage)}.
 *
 * @since 2.0
 */
final class ConfigurationContext {

	static final ConfigurationContext EMPTY_CONTEXT = new ConfigurationContext(null);

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationContext.class);

	private final Set<ConfigurationContext> childs = new HashSet<ConfigurationContext>(); // parent-child-tree

	private ThreadService threadService;

	ConfigurationContext(final AbstractCompositeStage compositeStage) {
		this.threadService = new ThreadService(compositeStage);
	}

	Map<Stage, String> getThreadableStages() {
		return threadService.getThreadableStages();
	}

	/**
	 * @see AbstractCompositeStage#addThreadableStage(Stage)
	 */
	final void addThreadableStage(final Stage stage, final String threadName) {
		addChildContext(stage);
		threadService.addThreadableStage(stage, threadName);
	}

	/**
	 * @see AbstractCompositeStage#connectPorts(OutputPort, InputPort, int)
	 */
	final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if (sourcePort.getOwningStage().getInputPorts().size() == 0) {
			if (!threadService.getThreadableStages().containsKey(sourcePort.getOwningStage())) {
				addThreadableStage(sourcePort.getOwningStage(), sourcePort.getOwningStage().getId());
			}
		}
		if (sourcePort.getPipe() != null || targetPort.getPipe() != null) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		addChildContext(sourcePort.getOwningStage());
		addChildContext(targetPort.getOwningStage());
		new InstantiationPipe(sourcePort, targetPort, capacity);
	}

	// FIXME: Rename method
	final void addChildContext(final Stage stage) {
		if (!stage.owningContext.equals(EMPTY_CONTEXT)) {
			if (stage.owningContext != this) { // Performance
				childs.add(stage.owningContext);
			}
		} else {
			stage.owningContext = this;
		}

	}

	final void finalizeContext() {
		for (ConfigurationContext child : childs) {
			child.finalizeContext();
			mergeContexts(child);
		}
	}

	final void initializeServices() {
		threadService.onInitialize();
	}

	private void mergeContexts(final ConfigurationContext child) {
		threadService.merge(child.getThreadService());

		// Finally copy parent services
		child.threadService = this.threadService;
	}

	void executeConfiguration() {
		this.threadService.onExecute();
	}

	void abortConfigurationRun() {
		this.threadService.onTerminate();
	}

	void waitForConfigurationToTerminate() {
		this.threadService.onFinish();
	}

	ThreadService getThreadService() {
		return threadService;
	}

	void setThreadService(final ThreadService threadService) {
		this.threadService = threadService;
	}

}
