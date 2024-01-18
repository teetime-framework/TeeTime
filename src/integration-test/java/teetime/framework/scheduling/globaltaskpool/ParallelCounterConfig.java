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

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.BlockingBusyWaitingRoundRobinDistributorStrategy;
import teetime.stage.basic.merger.Merger;
import teetime.stage.basic.merger.strategy.BlockingBusyWaitingRoundRobinMergerStrategy;

class ParallelCounterConfig extends Configuration {

	public ParallelCounterConfig(final int numElements, final int numParallelPipelines, final List<Integer> outputElements) {
		InitialElementProducer<Integer> init = new InitialElementProducer<>(numElements);
		Ramp ramp = new Ramp();
		Distributor<Integer> distributor = new Distributor<>(new BlockingBusyWaitingRoundRobinDistributorStrategy());
		// pipelines in between
		Merger<Integer> merger = new Merger<>(new BlockingBusyWaitingRoundRobinMergerStrategy());
		CollectorSink<Integer> sink = new CollectorSink<>(outputElements);

		from(init).to(ramp).end(distributor);

		for (int i = 0; i < numParallelPipelines; i++) {
			Counter<Integer> firstCounter = new Counter<>();
			Counter<Integer> secondCounter = new Counter<>();
			Cache<Integer> cache = new Cache<>();

			connectPorts(distributor.getNewOutputPort(), firstCounter.getInputPort());
			connectPorts(firstCounter.getOutputPort(), secondCounter.getInputPort());
			connectPorts(secondCounter.getOutputPort(), cache.getInputPort());
			connectPorts(cache.getOutputPort(), merger.getNewInputPort());
		}

		connectPorts(merger.getOutputPort(), sink.getInputPort());
	}
}
