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
package teetime.stage.basic.distributor.dynamic;

import teetime.framework.OutputPort;
import teetime.util.framework.port.PortAction;
import teetime.util.stage.OneTimeCondition;

public class RemovePortActionDistributor<T> implements PortAction<DynamicDistributor<T>> {

	private final OutputPort<T> outputPort;

	private final OneTimeCondition condition = new OneTimeCondition();

	public RemovePortActionDistributor(final OutputPort<T> outputPort) {
		if (null == outputPort) {
			throw new IllegalArgumentException("outputPort may not be null");
		}
		this.outputPort = outputPort;
	}

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		dynamicDistributor.removeDynamicPort(outputPort);
		condition.signalAll();
	}

	public void waitForCompletion() throws InterruptedException {
		condition.await();
	}

}
