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
package teetime.framework.termination;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.StageFacade;
import teetime.framework.Traverser;

public class NextActiveStageShouldTerminate extends TerminationCondition {

	private final AbstractStage stage;
	private final ActiveConsumerStageFinder visitor;
	private final AlwaysFalseCondition alwaysFalseCondition;

	public NextActiveStageShouldTerminate(final AbstractStage stage) {
		this.stage = stage;
		this.visitor = new ActiveConsumerStageFinder();
		this.alwaysFalseCondition = new AlwaysFalseCondition();
	}

	@Override
	public boolean isMet() {
		// traverse at runtime (!) until another active (consumer) stage was found
		Traverser traverser = new Traverser(visitor, alwaysFalseCondition);
		traverser.traverse(stage);
		InputPort<?> inputPortOfActiveStage = visitor.getActiveConsumerStageInputPort();

		AbstractStage activeStage = inputPortOfActiveStage.getOwningStage();
		for (InputPort<?> inputPort : StageFacade.INSTANCE.getInputPorts(activeStage)) {
			if (inputPort != inputPortOfActiveStage && !inputPort.isClosed()) { // NOPMD must test for identity
				return false;
			}
		}
		return true;
	}

}
