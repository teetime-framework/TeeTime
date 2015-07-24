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
package teetime.stage.basic.merger.dynamic;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.util.framework.port.PortAction;
import teetime.util.stage.OneTimeCondition;

public class CreatePortActionMerger<T> implements PortAction<DynamicMerger<T>> {

	private static final SpScPipeFactory INTER_THREAD_PIPE_FACTORY = new SpScPipeFactory();

	private final OneTimeCondition condition = new OneTimeCondition();

	private final OutputPort<T> outputPort;

	public CreatePortActionMerger(final OutputPort<T> outputPort) {
		this.outputPort = outputPort;
	}

	@Override
	public void execute(final DynamicMerger<T> dynamicDistributor) {
		InputPort<T> newInputPort = dynamicDistributor.getNewInputPort();

		onInputPortCreated(newInputPort);
		condition.signalAll();
	}

	private void onInputPortCreated(final InputPort<T> newInputPort) {
		INTER_THREAD_PIPE_FACTORY.create(outputPort, newInputPort);
	}

	public void waitForCompletion() throws InterruptedException {
		condition.await();
	}
}
