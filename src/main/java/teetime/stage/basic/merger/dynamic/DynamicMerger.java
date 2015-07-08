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

import java.util.concurrent.BlockingQueue;

import teetime.framework.DynamicInputPort;
import teetime.stage.basic.merger.Merger;
import teetime.stage.basic.merger.strategy.BusyWaitingRoundRobinStrategy;
import teetime.stage.basic.merger.strategy.IMergerStrategy;
import teetime.util.framework.port.PortAction;
import teetime.util.framework.port.PortActionHelper;

public class DynamicMerger<T> extends Merger<T> {

	protected final BlockingQueue<PortAction<DynamicMerger<T>>> portActions;

	public DynamicMerger() {
		this(new BusyWaitingRoundRobinStrategy());
	}

	public DynamicMerger(final IMergerStrategy strategy) {
		super(strategy);
		portActions = PortActionHelper.createPortActionQueue();
	}

	@Override
	protected void executeStage() {
		checkForPendingPortActionRequest(); // must be first to remove closed input ports
		super.executeStage();
	}

	protected void checkForPendingPortActionRequest() {
		PortActionHelper.checkForPendingPortActionRequest(this, portActions);
	}

	@Override
	public void removeDynamicPort(final DynamicInputPort<?> dynamicInputPort) { // make public
		super.removeDynamicPort(dynamicInputPort);
	}

	public boolean addPortActionRequest(final PortAction<DynamicMerger<T>> newPortActionRequest) {
		return portActions.offer(newPortActionRequest);
	}

}
