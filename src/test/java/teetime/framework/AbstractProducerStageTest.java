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
package teetime.framework;

import org.junit.Test;

import teetime.stage.basic.Sink;
import teetime.stage.basic.merger.Merger;

public class AbstractProducerStageTest {

	private static class FiniteProducer extends AbstractProducerStage<Object> {
		@Override
		protected void execute() {
			outputPort.send(Boolean.TRUE);
			terminateStage();
		}

		@Override
		public TerminationStrategy getTerminationStrategy() {
			return TerminationStrategy.BY_SELF_DECISION;
		}
	}

	private static class InfiniteProducer extends AbstractProducerStage<Object> {
		@Override
		protected void execute() {
			outputPort.send(Boolean.FALSE);
		}

		@Override
		public TerminationStrategy getTerminationStrategy() {
			return TerminationStrategy.BY_INTERRUPT;
		}
	}

	private static class MixedProducerConfig extends Configuration {
		public MixedProducerConfig() {
			FiniteProducer finiteProducer = new FiniteProducer();
			InfiniteProducer infiniteProducer = new InfiniteProducer();
			Merger<Object> merger = new Merger<>();

			connectPorts(finiteProducer.getOutputPort(), merger.createInputPort());
			connectPorts(infiniteProducer.getOutputPort(), merger.createInputPort());

			merger.declareActive();
		}
	}

	private static class TwoIndependentPipelinesConfig extends Configuration {
		public TwoIndependentPipelinesConfig() {
			FiniteProducer finiteProducer = new FiniteProducer();
			InfiniteProducer infiniteProducer = new InfiniteProducer();
			Sink<Object> sink0 = new Sink<>();
			Sink<Object> sink1 = new Sink<>();

			connectPorts(finiteProducer.getOutputPort(), sink0.getInputPort());
			connectPorts(infiniteProducer.getOutputPort(), sink1.getInputPort());
		}
	}

	/*
	 * Use a t/o since the execution may not block infinitely;
	 * expected execution time is 500 ms, so the t/o should be sufficiently high
	 */
	@Test(timeout = 5000)
	public void shouldTerminateFiniteAndInfiniteProducer() {
		MixedProducerConfig config = new MixedProducerConfig();
		new Execution<>(config).executeBlocking();
	}

	/*
	 * Use a t/o since the execution may not block infinitely;
	 * expected execution time is 500 ms, so the t/o should be sufficiently high
	 */
	@Test(timeout = 5000)
	public void shouldTerminateTwoIndependentPipelines() {
		TwoIndependentPipelinesConfig config = new TwoIndependentPipelinesConfig();
		new Execution<>(config).executeBlocking();
	}
}
