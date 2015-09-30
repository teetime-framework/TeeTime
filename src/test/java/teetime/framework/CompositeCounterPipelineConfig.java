package teetime.framework;

import teetime.stage.InitialElementProducer;
import teetime.stage.basic.Sink;

class CompositeCounterPipelineConfig extends Configuration {

	private final InitialElementProducer<CounterContainer> producer;

	public CompositeCounterPipelineConfig(final int numMaxCounterIncrementors, final CounterContainer element) {
		if (element == null) {
			throw new IllegalArgumentException("element may not be null");
		}

		producer = new InitialElementProducer<CounterContainer>(element);
		CompositeCounterIncrementer compositeCounterIncrementor = new CompositeCounterIncrementer(numMaxCounterIncrementors);
		Sink<CounterContainer> sink = new Sink<CounterContainer>();

		connectPorts(producer.getOutputPort(), compositeCounterIncrementor.getInputPort());
		connectPorts(compositeCounterIncrementor.getOutputPort(), sink.getInputPort());
	}

	public InitialElementProducer<CounterContainer> getProducer() {
		return producer;
	}
}
