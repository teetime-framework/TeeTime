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
package teetime.framework.scheduling.pushpullmodel;

import java.util.HashSet;
import java.util.Set;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

class IntraStageCollector implements ITraverserVisitor {

	private final Set<AbstractStage> intraStages = new HashSet<AbstractStage>();
	private final AbstractStage startStage;

	public IntraStageCollector(final AbstractStage startStage) {
		super();
		this.startStage = startStage;
	}

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		// if (stage.equals(startStage) || stage.getOwningThread() == null /* before execution */
		// || stage.getOwningThread() == startStage.getOwningThread() /* while execution */) {
		if (stage.equals(startStage) || !stage.isActive()) {
			intraStages.add(stage);
			return VisitorBehavior.CONTINUE_FORWARD; // NOPMD two return stmts make the code clearer to understand
		}
		return VisitorBehavior.STOP;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

	public Set<AbstractStage> getIntraStages() {
		return intraStages;
	}

}
