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

import java.util.HashMap;
import java.util.Map;

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

	public static final ConfigurationContext EMPTY_CONTEXT = new ConfigurationContext();

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationContext.class);

	private Map<Stage, String> threadableStages = new HashMap<Stage, String>();

	ConfigurationContext() {}

	Map<Stage, String> getThreadableStages() {
		return this.threadableStages;
	}

	/**
	 * @see AbstractCompositeStage#addThreadableStage(Stage)
	 */
	final void addThreadableStage(final Stage stage, final String threadName) {
		mergeContexts(stage);
		if (this.threadableStages.put(stage, threadName) != null) {
			LOGGER.warn("Stage " + stage.getId() + " was already marked as threadable stage.");
		}
	}

	/**
	 * @see AbstractCompositeStage#connectPorts(OutputPort, InputPort, int)
	 */
	final <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		if (sourcePort.getOwningStage().getInputPorts().length == 0) {
			if (!threadableStages.containsKey(sourcePort.getOwningStage())) {
				addThreadableStage(sourcePort.getOwningStage(), sourcePort.getOwningStage().getId());
			}
		}
		if (sourcePort.getPipe() != null || targetPort.getPipe() != null) {
			LOGGER.warn("Overwriting existing pipe while connecting stages " +
					sourcePort.getOwningStage().getId() + " and " + targetPort.getOwningStage().getId() + ".");
		}
		mergeContexts(sourcePort.getOwningStage());
		mergeContexts(targetPort.getOwningStage());
		new InstantiationPipe(sourcePort, targetPort, capacity);
	}

	final void mergeContexts(final Stage stage) {
		if (!stage.owningContext.equals(EMPTY_CONTEXT)) {
			if (stage.owningContext != this) { // Performance
				this.threadableStages.putAll(stage.owningContext.threadableStages);
				stage.owningContext.threadableStages = this.threadableStages;
			}
		} else {
			stage.owningContext = this;
		}

	}

}
