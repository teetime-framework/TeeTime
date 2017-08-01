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
package teetime.framework.scheduling.globaltaskqueue;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.*;
import teetime.framework.pipe.DummyPipe;

/**
 * Created by nilsziermann on 30.12.16.
 */
class TaskQueueA1StageCollector implements ITraverserVisitor {

	private final Set<AbstractStage> stages = new HashSet<AbstractStage>();

	public Set<AbstractStage> getStages() {
		return this.stages;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractStage stage) {
		if (!stages.contains(stage) && stage.getCurrentState() == StageState.CREATED) {
			stages.add(stage);
		}
		// visitor termination condition: stop if the stage already runs or has been terminated
		return stage.getCurrentState() == StageState.CREATED ? Traverser.VisitorBehavior.CONTINUE_BACK_AND_FORTH : Traverser.VisitorBehavior.STOP;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractPort<?> port) {
		return Traverser.VisitorBehavior.CONTINUE_BACK_AND_FORTH;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

}
