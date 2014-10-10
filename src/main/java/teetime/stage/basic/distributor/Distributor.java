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

package teetime.stage.basic.distributor;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 * 
 * @param T
 *            the type of the input port and the output ports
 */
public class Distributor<T> extends ConsumerStage<T> {

	private IDistributorStrategy<T> strategy = new RoundRobinStrategy<T>();

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final T element) {
		this.strategy.distribute((OutputPort<T>[]) this.getOutputPorts(), element);
	}

	@Override
	public void onIsPipelineHead() {
		// for (OutputPort<T> op : this.outputPortList) {
		// op.getPipe().close();
		// System.out.println("End signal sent, size: " + op.getPipe().size());
		// }
	}

	public OutputPort<T> getNewOutputPort() {
		return this.createOutputPort();
	}

	public IDistributorStrategy<T> getStrategy() {
		return this.strategy;
	}

	public void setStrategy(final IDistributorStrategy<T> strategy) {
		this.strategy = strategy;
	}

}
