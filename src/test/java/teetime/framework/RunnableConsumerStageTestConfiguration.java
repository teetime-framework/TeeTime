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
package teetime.framework;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class RunnableConsumerStageTestConfiguration extends Configuration {

	private final List<Integer> collectedElements = new ArrayList<Integer>();
	private final CollectorSink<Integer> collectorSink;

	public RunnableConsumerStageTestConfiguration(final Integer... inputElements) {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(inputElements);
		if (inputElements.length > 0) {
			producer.declareActive();
		}

		CollectorSink<Integer> collectorSink = new CollectorSink<Integer>(collectedElements);
		collectorSink.declareActive();

		// Can not use createPorts, as the if condition above will lead to an exception
		IPipe pipe = new SpScPipeFactory().create(producer.getOutputPort(), collectorSink.getInputPort());
		registerCustomPipe((AbstractPipe<?>) pipe);

		this.collectorSink = collectorSink;
	}

	public List<Integer> getCollectedElements() {
		return collectedElements;
	}

	public Thread getConsumerThread() {
		return collectorSink.getOwningThread();
	}
}
