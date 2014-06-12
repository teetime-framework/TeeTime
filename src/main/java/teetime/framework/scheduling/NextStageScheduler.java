/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package teetime.framework.scheduling;

import java.util.LinkedHashSet;
import java.util.Set;

import teetime.framework.concurrent.IStageScheduler;
import teetime.framework.concurrent.IStageWorkList;
import teetime.framework.concurrent.StageWorkArrayList;
import teetime.framework.core.IOutputPort;
import teetime.framework.core.IPipeline;
import teetime.framework.core.IStage;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public final class NextStageScheduler implements IStageScheduler {

	private final Set<IStage> highestPrioritizedEnabledStages = new LinkedHashSet<IStage>();
	private final IStageWorkList workList;
	private final StageStateManager stageStateManager;

	public NextStageScheduler(final IPipeline pipeline, final int accessesDeviceId, final StageStateManager stageStateManager) {
		this.stageStateManager = stageStateManager;
		// this.workList = new StageWorkList(accessesDeviceId, pipeline.getStages().size());
		this.workList = new StageWorkArrayList(pipeline, accessesDeviceId); // faster implementation

		this.highestPrioritizedEnabledStages.addAll(pipeline.getStartStages());

		this.workList.pushAll(this.highestPrioritizedEnabledStages);
		// System.out.println("Initial work list: " + this.workList);
		// this.workList.addAll(pipeline.getStages());
	}

	@Override
	public final IStage get() {
		return this.workList.read();
	}

	@Override
	public final boolean isAnyStageActive() {
		return !this.workList.isEmpty();
	}

	@Override
	public final void disable(final IStage stage) {
		this.stageStateManager.disable(stage);

		if (this.highestPrioritizedEnabledStages.contains(stage)) {
			this.highestPrioritizedEnabledStages.remove(stage);
			for (final IStage outputStage : stage.getAllOutputStages()) {
				if (this.stageStateManager.isStageEnabled(outputStage)) {
					this.highestPrioritizedEnabledStages.add(outputStage);
				}
			}
		}

		// System.out.println("highestPrioritizedEnabledStages: "+this.highestPrioritizedEnabledStages);
		stage.fireSignalClosingToAllOutputPorts();
	}

	@Override
	public final void determineNextStage(final IStage stage, final boolean executedSuccessfully) {
		// final Collection<? extends IStage> outputStages = stage.getContext().getOutputStages();
		final IOutputPort<?, ?>[] outputPorts = stage.getContext().getOutputPorts();
		if (outputPorts.length > 0) {
			final boolean inputPortsAreEmpty = stage.getContext().inputPortsAreEmpty();
			if (inputPortsAreEmpty) {
				this.workList.pop();
			}

			// TODO consider to not add the stage again if it has a cyclic pipe
			// TODO or prioritize non-self stages
			// while (outputStages.remove(stage)) {
			// }

			this.workList.pushAll(outputPorts);

			stage.getContext().clearSucessors();
		} else {
			this.workList.pop();
		}

		if (this.workList.isEmpty()) {
			this.workList.pushAll(this.highestPrioritizedEnabledStages);
		}

	}
}
