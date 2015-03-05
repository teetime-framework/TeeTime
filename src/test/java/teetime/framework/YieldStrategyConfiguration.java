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
package teetime.framework;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.Relay;

class YieldStrategyConfiguration extends AnalysisConfiguration {
	private final IPipeFactory intraThreadPipeFactory;
	private final IPipeFactory interThreadPipeFactory;

	private CollectorSink<Object> collectorSink;

	public YieldStrategyConfiguration(final Object... elements) {
		intraThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		interThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

		InitialElementProducer<Object> producer = buildProducer(elements);
		addThreadableStage(producer);

		Stage consumer = buildConsumer(producer);
		addThreadableStage(consumer);
	}

	private InitialElementProducer<Object> buildProducer(final Object... elements) {
		InitialElementProducer<Object> initialElementProducer = new InitialElementProducer<Object>(elements);

		return initialElementProducer;
	}

	private Relay<Object> buildConsumer(final InitialElementProducer<Object> producer) {
		Relay<Object> relay = new Relay<Object>();
		CollectorSink<Object> collectorSink = new CollectorSink<Object>();

		// relay.setIdleStrategy(new YieldStrategy());

		interThreadPipeFactory.create(producer.getOutputPort(), relay.getInputPort());
		intraThreadPipeFactory.create(relay.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;

		return relay;
	}

	public CollectorSink<Object> getCollectorSink() {
		return collectorSink;
	}
}
