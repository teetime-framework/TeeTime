/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BreadthFirstTraverser {

	public void traverse(final AbstractStage startStage, final ITraverserVisitor traverserVisitor) {
		final Set<AbstractStage> visitedStages = new HashSet<AbstractStage>();
		final Queue<AbstractStage> queue = new LinkedList<>();

		queue.add(startStage);
		visitedStages.add(startStage);

		while (!queue.isEmpty()) {
			AbstractStage stage = queue.remove();
			traverserVisitor.visit(startStage);

			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();

				if (!visitedStages.contains(targetStage)) {
					queue.add(targetStage);
					visitedStages.add(startStage);
				}

				traverserVisitor.visit(outputPort);
			}
		}
	}
}
