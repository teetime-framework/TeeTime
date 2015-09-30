/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage.basic.distributor.dynamic;

import java.util.concurrent.BlockingQueue;

import teetime.framework.OutputPort;
import teetime.framework.signal.TerminatingSignal;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.IDistributorStrategy;
import teetime.stage.basic.distributor.strategy.RoundRobinStrategy2;
import teetime.util.framework.port.PortAction;
import teetime.util.framework.port.PortActionHelper;
import teetime.util.framework.port.PortRemovedListener;

public class DynamicDistributor<T> extends Distributor<T> implements PortRemovedListener<OutputPort<?>> {

	protected final BlockingQueue<PortAction<DynamicDistributor<T>>> portActions;

	/**
	 * Uses {@link RoundRobinStrategy2} as default distributor strategy.
	 */
	public DynamicDistributor() {
		this(new RoundRobinStrategy2());
	}

	public DynamicDistributor(final IDistributorStrategy strategy) {
		super(strategy);
		this.portActions = PortActionHelper.createPortActionQueue();
		addOutputPortRemovedListener(this);
	}

	@Override
	protected void execute(final T element) {
		checkForPendingPortActionRequest();

		super.execute(element);
	}

	protected void checkForPendingPortActionRequest() {
		PortActionHelper.checkForPendingPortActionRequest(this, portActions);
	}

	@Override
	public void removeDynamicPort(final OutputPort<?> outputPort) { // make public
		super.removeDynamicPort(outputPort);
	}

	public boolean addPortActionRequest(final PortAction<DynamicDistributor<T>> newPortActionRequest) {
		return portActions.offer(newPortActionRequest);
	}

	@Override
	public void onPortRemoved(final OutputPort<?> removedOutputPort) {
		removedOutputPort.sendSignal(new TerminatingSignal());
	}
}
