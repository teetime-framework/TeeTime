/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.SingleElementPipe;
import teetime.framework.pipe.SpScPipe;
import teetime.framework.pipe.UnboundedSpScPipe;

class ExecutionInstantiation {

	private static final int DEFAULT_COLOR = 0;

	private final ConfigurationContext context;

	private ExecutionInstantiation(final ConfigurationContext context) {
		this.context = context;
	}

	void instantiatePipes() {
		int color = DEFAULT_COLOR;
		Map<AbstractStage, Integer> colors = new HashMap<AbstractStage, Integer>();
		Set<AbstractStage> threadableStages = context.getThreadableStages();
		for (AbstractStage threadableStage : threadableStages) {
			color++;
			colors.put(threadableStage, color);

			ThreadPainter threadPainter = new ThreadPainter(colors, color, threadableStages);
			threadPainter.colorAndConnectStages(threadableStage);
		}
	}

	private static class ThreadPainter {

		private final Map<AbstractStage, Integer> colors;
		private final int color;
		private final Set<AbstractStage> threadableStages;

		public ThreadPainter(final Map<AbstractStage, Integer> colors, final int color, final Set<AbstractStage> threadableStages) {
			super();
			this.colors = colors;
			this.color = color;
			this.threadableStages = threadableStages;
		}

		public int colorAndConnectStages(final AbstractStage stage) {
			int createdConnections = 0;

			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				if (outputPort.pipe != null && outputPort.pipe instanceof InstantiationPipe) {
					InstantiationPipe<?> pipe = (InstantiationPipe<?>) outputPort.pipe;
					createdConnections += processPipe(outputPort, pipe);
					createdConnections++;
				}
			}

			return createdConnections;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private int processPipe(final OutputPort outputPort, final InstantiationPipe pipe) {
			int numCreatedConnections;

			AbstractStage targetStage = pipe.getTargetPort().getOwningStage();
			int targetColor = colors.containsKey(targetStage) ? colors.get(targetStage) : DEFAULT_COLOR;

			if (threadableStages.contains(targetStage) && targetColor != color) {
				if (pipe.capacity() != 0) {
					new SpScPipe(outputPort, pipe.getTargetPort(), pipe.capacity());
				} else {
					new UnboundedSpScPipe(outputPort, pipe.getTargetPort());
				}
				numCreatedConnections = 0;
			} else {
				if (colors.containsKey(targetStage)) {
					if (!colors.get(targetStage).equals(color)) {
						throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (but not its "headstage")
					}
				}
				new SingleElementPipe(outputPort, pipe.getTargetPort());
				colors.put(targetStage, color);
				numCreatedConnections = colorAndConnectStages(targetStage);
			}

			return numCreatedConnections;
		}

	}

}
