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
import java.util.Set;

import teetime.framework.pipe.DummyPipe;

/**
 * Traverses the all stages that are <b>reachable</b> from the given <i>stage</i>.
 * Each stage is visited exactly once (not more, not less).
 *
 * @author Christian Wulf
 *
 */
public class Traverser {

	public static enum VisitorBehavior {
		CONTINUE_FORWARD, CONTINUE_BACKWARD, CONTINUE_BACK_AND_FORTH, STOP
	}

	private final Set<AbstractStage> visitedStages = new HashSet<AbstractStage>();

	private final ITraverserVisitor traverserVisitor;

	public Traverser(final ITraverserVisitor traverserVisitor) {
		this.traverserVisitor = traverserVisitor;
	}

	public void traverse(final AbstractStage stage) {
		// termination condition: stop if the stage already runs or has been terminated
		if (stage.getCurrentState() != StageState.CREATED) {
			return; // NOPMD sequential termination conditions are more readable
		}

		if (!visitedStages.add(stage)) {
			return;
		}

		VisitorBehavior behavior = traverserVisitor.visit(stage);
		if (behavior == VisitorBehavior.STOP) {
			return;
		}

		if (behavior == VisitorBehavior.CONTINUE_FORWARD || behavior == VisitorBehavior.CONTINUE_BACK_AND_FORTH) {
			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				visitAndTraverse(outputPort, VisitorBehavior.CONTINUE_FORWARD);
			}
		}

		if (behavior == VisitorBehavior.CONTINUE_BACKWARD || behavior == VisitorBehavior.CONTINUE_BACK_AND_FORTH) {
			for (InputPort<?> inputPort : stage.getInputPorts()) {
				visitAndTraverse(inputPort, VisitorBehavior.CONTINUE_BACKWARD);
			}
		}
	}

	private void visitAndTraverse(final AbstractPort<?> port, final VisitorBehavior direction) {
		if (port.getPipe() == null) {
			throw new IllegalStateException("2003 - The port " + port + " is not connected with another port.");
		}

		if (port.getPipe() instanceof DummyPipe) {
			traverserVisitor.visit((DummyPipe) port.getPipe(), port);
			return;
		}

		VisitorBehavior behavior = traverserVisitor.visit(port);

		if (behavior != VisitorBehavior.STOP) {
			AbstractPort<?> nextPort = (direction == VisitorBehavior.CONTINUE_FORWARD) ? port.getPipe().getTargetPort() : port.getPipe().getSourcePort();

			traverse(nextPort.getOwningStage()); // recursive call
		}
	}

	/**
	 * For testing purposes only.
	 *
	 * @return the visited stages
	 */
	/* default */ Set<AbstractStage> getVisitedStages() {
		return visitedStages;
	}

}
