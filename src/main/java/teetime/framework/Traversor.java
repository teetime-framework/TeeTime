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
import java.util.Set;

import teetime.framework.IPipeVisitor.VisitorBehavior;
import teetime.framework.pipe.IPipe;

public class Traversor {

	public static enum Direction {
		BACKWARD, FORWARD, BOTH
	}

	private final IPipeVisitor pipeVisitor;
	private final Direction direction;
	private final Set<Stage> visitedStages = new HashSet<Stage>();

	public Traversor(final IPipeVisitor pipeVisitor) {
		this(pipeVisitor, Direction.FORWARD);
	}

	public Traversor(final IPipeVisitor pipeVisitor, final Direction direction) {
		this.pipeVisitor = pipeVisitor;
		this.direction = direction;
	}

	public void traverse(final Stage stage) {
		if (!visitedStages.add(stage)) {
			return;
		}

		if (direction == Direction.BOTH || direction == Direction.FORWARD) {
			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				visitAndTraverse(outputPort);
			}
		}

		if (direction == Direction.BOTH || direction == Direction.BACKWARD) {
			for (InputPort<?> inputPort : stage.getInputPorts()) {
				visitAndTraverse(inputPort);
			}
		}
	}

	private void visitAndTraverse(final AbstractPort<?> port) {
		IPipe pipe = port.getPipe();
		if (null != pipe && pipeVisitor.visit(pipe) == VisitorBehavior.CONTINUE) {
			Stage owningStage = pipe.getTargetPort().getOwningStage();
			traverse(owningStage); // recursive call
		}
	}

	public Set<Stage> getVisitedStage() {
		return visitedStages;
	}
}
