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

	private final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();

	private final ConfigurationContext configuration;

	public ExecutionInstantiation(final ConfigurationContext configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings("rawtypes")
	Integer colorAndConnectStages(final Integer i, final Map<Stage, Integer> colors, final Stage threadableStage, final ConfigurationContext configuration) {
		Integer createdConnections = new Integer(0);
		Set<Stage> threadableStageJobs = configuration.getThreadableStages();
		for (OutputPort outputPort : threadableStage.getOutputPorts()) {
			if (outputPort.pipe != null) {
				if (outputPort.pipe instanceof InstantiationPipe) {
					InstantiationPipe pipe = (InstantiationPipe) outputPort.pipe;
					Stage targetStage = pipe.getTargetPort().getOwningStage();
					Integer targetColor = new Integer(0);
					if (colors.containsKey(targetStage)) {
						targetColor = colors.get(targetStage);
					}
					if (threadableStageJobs.contains(targetStage) && targetColor.compareTo(i) != 0) {
						if (pipe.getCapacity() != 0) {
							interBoundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), pipe.getCapacity());
						} else {
							interUnboundedThreadPipeFactory.create(outputPort, pipe.getTargetPort(), 4);
						}
					} else {
						if (colors.containsKey(targetStage)) {
							if (!colors.get(targetStage).equals(i)) {
								throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (but not its "headstage")
							}
						}
						intraThreadPipeFactory.create(outputPort, pipe.getTargetPort());
						colors.put(targetStage, i);
						createdConnections += colorAndConnectStages(i, colors, targetStage, configuration);
					}
					createdConnections++;
				}
			}

		}
		return createdConnections;
	}

	void instantiatePipes() {
		Integer i = new Integer(0);
		Map<Stage, Integer> colors = new HashMap<Stage, Integer>();
		Set<Stage> threadableStageJobs = configuration.getThreadableStages();
		Integer createdConnections = 0;
		for (Stage threadableStage : threadableStageJobs) {
			i++;
			colors.put(threadableStage, i);
			createdConnections = colorAndConnectStages(i, colors, threadableStage, configuration);
		}
		LOGGER.debug("Created " + createdConnections + " connections");
	}

}
