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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.pipe.UnboundedSpScPipeFactory;

class ExecutionInstantiation {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionInstantiation.class);
	private static final int DEFAULT_COLOR = 0;

	private final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();

	private final ConfigurationContext configuration;

	public ExecutionInstantiation(final ConfigurationContext configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings({ "rawtypes" })
	int colorAndConnectStages(final int color, final Map<Stage, Integer> colors, final Stage threadableStage, final ConfigurationContext configuration) {
		Set<Stage> threadableStages = configuration.getThreadableStages().keySet();

		int createdConnections = 0;
		for (OutputPort outputPort : threadableStage.getOutputPorts()) {
			if (outputPort.pipe != null) {
				if (outputPort.pipe instanceof InstantiationPipe) {
					InstantiationPipe pipe = (InstantiationPipe) outputPort.pipe;
					createdConnections += processPipe(color, colors, configuration, threadableStages, outputPort, pipe);
					createdConnections++;
				}
			}

		}
		return createdConnections;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int processPipe(final int color, final Map<Stage, Integer> colors, final ConfigurationContext configuration, final Set<Stage> threadableStages,
			final OutputPort outputPort, final InstantiationPipe pipe) {
		Stage targetStage = pipe.getTargetPort().getOwningStage();

		int targetColor = DEFAULT_COLOR;
		if (colors.containsKey(targetStage)) {
			targetColor = colors.get(targetStage);
		}

		if (threadableStages.contains(targetStage) && targetColor != color) {
			if (pipe.capacity() != 0) {
				interBoundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), pipe.capacity());
			} else {
				interUnboundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), 4);
			}
		} else {
			if (colors.containsKey(targetStage)) {
				if (!colors.get(targetStage).equals(color)) {
					throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (but not its "headstage")
				}
			}
			intraThreadPipeFactory.create(outputPort, pipe.getTargetPort());
			colors.put(targetStage, color);
			return colorAndConnectStages(color, colors, targetStage, configuration);
		}
		return 0;
	}

	void instantiatePipes() {
		int color = DEFAULT_COLOR;
		Map<Stage, Integer> colors = new HashMap<Stage, Integer>();
		Set<Stage> threadableStageJobs = configuration.getThreadableStages().keySet();
		int numCreatedConnections = 0;
		for (Stage threadableStage : threadableStageJobs) {
			color++;
			colors.put(threadableStage, color);
			numCreatedConnections += colorAndConnectStages(color, colors, threadableStage, configuration);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created " + numCreatedConnections + " connections");
		}
	}

}
