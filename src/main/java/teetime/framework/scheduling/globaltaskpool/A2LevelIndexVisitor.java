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
package teetime.framework.scheduling.globaltaskpool;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.StageFacade;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

class A2LevelIndexVisitor implements ITraverserVisitor {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private int maxLevelIndex;

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		AbstractStage sourceStage = port.getOwningStage();
		AbstractStage targetStage = port.getPipe().getTargetPort().getOwningStage();

		int targetLevelIndex = STAGE_FACADE.getLevelIndex(targetStage);
		int sourceLevelIndex = STAGE_FACADE.getLevelIndex(sourceStage);
		int levelIndex = Math.max(targetLevelIndex, sourceLevelIndex + 1);
		STAGE_FACADE.setLevelIndex(targetStage, levelIndex);

		maxLevelIndex = Math.max(maxLevelIndex, levelIndex);

		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

	public int getMaxLevelIndex() {
		return maxLevelIndex;
	}

}
