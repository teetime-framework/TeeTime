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
import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;

public class Traverser {

	public static enum Direction {
		BACKWARD(1), FORWARD(2), BOTH(BACKWARD.value | FORWARD.value);

		private final int value;

		private Direction(final int value) {
			this.value = value;
		}

		public boolean represents(final Direction direction) {
			return (value & direction.value) == direction.value;
		}
	}

	private static final IPortVisitor DEFAULT_PORT_VISITOR = new IPortVisitor() {
		@Override
		public void visit(final AbstractPort<?> port) {
			// do nothing
		}
	};

	private final IPortVisitor portVisitor;
	private final IPipeVisitor pipeVisitor;
	private final Direction direction;
	private final Set<Stage> visitedStages = new HashSet<Stage>();

	public Traverser(final IPipeVisitor pipeVisitor) {
		this(pipeVisitor, Direction.FORWARD);
	}

	public Traverser(final IPipeVisitor pipeVisitor, final Direction direction) {
		this(DEFAULT_PORT_VISITOR, pipeVisitor, direction);
	}

	public Traverser(final IPortVisitor portVisitor, final IPipeVisitor pipeVisitor, final Direction direction) {
		this.portVisitor = portVisitor;
		this.pipeVisitor = pipeVisitor;
		this.direction = direction;
	}

	public void traverse(final Stage stage) {
		if (!visitedStages.add(stage) || stage.getCurrentState() != StageState.CREATED) {
			// do not visit (1) an already visited stage and (2) a stage that currently run (runtime visiting)
			return;
		}

		if (direction.represents(Direction.FORWARD)) {
			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				visitAndTraverse(outputPort, Direction.FORWARD);
			}
		}

		if (direction.represents(Direction.BACKWARD)) {
			for (InputPort<?> inputPort : stage.getInputPorts()) {
				visitAndTraverse(inputPort, Direction.BACKWARD);
			}
		}
	}

	private void visitAndTraverse(final AbstractPort<?> port, final Direction direction) {
		portVisitor.visit(port);
		IPipe<?> pipe = port.getPipe();
		if (pipe != DummyPipe.INSTANCE && pipeVisitor.visit(pipe) == VisitorBehavior.CONTINUE) {
			AbstractPort<?> nextPort = (direction == Direction.FORWARD) ? pipe.getTargetPort() : pipe.getSourcePort();
			traverse(nextPort.getOwningStage()); // recursive call
		}
	}

	public Set<Stage> getVisitedStages() {
		return visitedStages;
	}
}
