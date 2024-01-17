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
package teetime.stage.basic.merger.dynamic;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.BoundedSynchedPipe;
import teetime.util.framework.port.PortAction;
import teetime.util.stage.OneTimeCondition;

public class CreatePortActionMerger<T> implements PortAction<DynamicMerger<T>> {

	private final OneTimeCondition condition = new OneTimeCondition();

	private final OutputPort<T> outputPort;
	private final int capacity;

	public CreatePortActionMerger(final OutputPort<T> outputPort, final int capacity) {
		super();
		this.outputPort = outputPort;
		this.capacity = capacity;
	}

	@Override
	public void execute(final DynamicMerger<T> dynamicDistributor) {
		InputPort<T> newInputPort = dynamicDistributor.getNewInputPort();

		new BoundedSynchedPipe<>(outputPort, newInputPort, capacity);

		condition.signalAll();
	}

	public void waitForCompletion() throws InterruptedException {
		condition.await();
	}
}
