package teetime.framework.test;

import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

class StageFactory {

	private StageFactory() {
		// factory class
	}

	static <T> InitialElementProducer<T> createProducer(final List<T> inputElements) {
		InitialElementProducer<T> producer = new InitialElementProducer<T>(inputElements);
		return producer;
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
		CollectorSink<T> sink = new CollectorSink<T>(outputElements);
		return sink;
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
