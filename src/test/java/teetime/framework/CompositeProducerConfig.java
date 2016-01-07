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

import java.util.List;

import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

class CompositeProducerConfig extends Configuration {

	private final CollectorSink<Integer> sink;

	public CompositeProducerConfig() {
		InitialElementProducer<Integer> initialElementProducer = new InitialElementProducer<Integer>(0, 1, 2, 3, 4);
		CompositeProducerStage<Integer> compositeProducerStage = new CompositeProducerStage<Integer>(5, 6, 7, 8, 9);
		sink = new CollectorSink<Integer>();

		connectPorts(initialElementProducer.getOutputPort(), compositeProducerStage.getInputPort());
		connectPorts(compositeProducerStage.getOutputPort(), sink.getInputPort());
	}

	List<Integer> getResultElements() {
		return sink.getElements();
	}
}
