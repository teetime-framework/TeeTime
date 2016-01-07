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
package teetime.framework;

import teetime.stage.Clock;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.Relay;
import teetime.stage.basic.Delay;

class WaitStrategyConfiguration extends Configuration {

	private Delay<Object> delay;
	private CollectorSink<Object> collectorSink;

	public WaitStrategyConfiguration(final long initialDelayInMs, final Object... elements) {

		AbstractStage producer = buildProducer(elements);
		producer.declareActive();

		AbstractStage consumer = buildConsumer(delay);
		consumer.declareActive();

		Clock clock = buildClock(initialDelayInMs, delay);
		clock.declareActive();
	}

	private Clock buildClock(final long initialDelayInMs, final Delay<Object> delay) {
		Clock clock = new Clock();
		clock.setInitialDelayInMs(initialDelayInMs);

		connectPorts(clock.getOutputPort(), delay.getTimestampTriggerInputPort());

		return clock;
	}

	private AbstractStage buildProducer(final Object... elements) {
		InitialElementProducer<Object> initialElementProducer = new InitialElementProducer<Object>(elements);
		delay = new Delay<Object>();

		connectPorts(initialElementProducer.getOutputPort(), delay.getInputPort());

		return initialElementProducer;
	}

	private Relay<Object> buildConsumer(final Delay<Object> delay) {
		Relay<Object> relay = new Relay<Object>();
		CollectorSink<Object> collectorSink = new CollectorSink<Object>();

		// relay.setIdleStrategy(new WaitStrategy(relay));

		connectPorts(delay.getOutputPort(), relay.getInputPort());
		connectPorts(relay.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;

		return relay;
	}

	public CollectorSink<Object> getCollectorSink() {
		return collectorSink;
	}
}
