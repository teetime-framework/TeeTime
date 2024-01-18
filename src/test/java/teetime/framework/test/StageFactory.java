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
package teetime.framework.test;

import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

final class StageFactory {

	private StageFactory() {
		// factory class
	}

	static <T> InitialElementProducer<T> createProducer(final List<T> inputElements) {
		return new InitialElementProducer<T>(inputElements);
	}

	@SuppressWarnings("unchecked")
	static <T> InitialElementProducer<T> getProducerFromInputPort(final InputPort<T> inputPort) {
		OutputPort<?> sourcePort = inputPort.getPipe().getSourcePort();
		AbstractStage owningStage = sourcePort.getOwningStage();
		if (owningStage instanceof InitialElementProducer) {
			return (InitialElementProducer<T>) owningStage;
		}

		String message = String.format("%s", owningStage);
		throw new IllegalArgumentException(message);
	}

	static <T> CollectorSink<T> createSink(final List<T> outputElements) {
		return new CollectorSink<T>(outputElements);
	}

	@SuppressWarnings("unchecked")
	static <T> CollectorSink<T> getSinkFromOutputPort(final OutputPort<T> outputPort) {
		InputPort<?> targetPort = outputPort.getPipe().getTargetPort();
		AbstractStage owningStage = targetPort.getOwningStage();
		if (owningStage instanceof CollectorSink) {
			return (CollectorSink<T>) owningStage;
		}

		String message = String.format("%s", owningStage);
		throw new IllegalArgumentException(message);
	}
}
