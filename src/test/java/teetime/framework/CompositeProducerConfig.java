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
