/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.stage.basic.distributor;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Christian Wulf
 *
 * @since 1.0
 *
 * @param T
 *            the type of the input port and the output ports
 */
public final class Distributor<T> extends AbstractConsumerStage<T> {

	private IDistributorStrategy strategy;

	public Distributor() {
		this(new RoundRobinStrategy());
	}

	public Distributor(final IDistributorStrategy strategy) {
		this.strategy = strategy;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final T element) {
		this.strategy.distribute((OutputPort<T>[]) this.getOutputPorts(), element);
	}

	public OutputPort<T> getNewOutputPort() {
		return this.createOutputPort();
	}

	public IDistributorStrategy getStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IDistributorStrategy strategy) {
		this.strategy = strategy;
	}

}
