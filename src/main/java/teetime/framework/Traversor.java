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

import java.util.HashSet;
import java.util.Set;

import teetime.framework.IStageVisitor.VisitorBehavior;
import teetime.framework.pipe.IPipe;

public class Traversor {

	private final IStageVisitor stageVisitor;
	private final Set<Stage> visitedStage = new HashSet<Stage>();

	public Traversor(final IStageVisitor stageVisitor) {
		this.stageVisitor = stageVisitor;
	}

	public void traverse(final Stage stage, final IPipe inputPipe) {

		VisitorBehavior visitorBehavior = stageVisitor.visit(stage, inputPipe);
		if (visitorBehavior == VisitorBehavior.STOP) {
			return;
		}

		if (!visitedStage.contains(stage)) {
			visitedStage.add(stage);
		} else {
			return;
		}

		OutputPort<?>[] outputPorts = stage.getOutputPorts();
		for (OutputPort<?> outputPort : outputPorts) {
			IPipe pipe = outputPort.getPipe();
			if (null != pipe) {
				Stage owningStage = pipe.getTargetPort().getOwningStage();
				traverse(owningStage, pipe); // recursive call
			}
		}
	}

	public Set<Stage> getVisitedStage() {
		return visitedStage;
	}
}
