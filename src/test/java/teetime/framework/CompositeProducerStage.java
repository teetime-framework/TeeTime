package teetime.framework;

import teetime.stage.InitialElementProducer;
import teetime.stage.basic.merger.Merger;

/**
 * A composite stage that contains a producer and an active merger.
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the element type of the producer
 */
class CompositeProducerStage<T> extends AbstractCompositeStage {

	private final Merger<T> merger;

	public CompositeProducerStage(final T... elements) {
		InitialElementProducer<T> producer = new InitialElementProducer<T>(elements);
		merger = new Merger<T>();

		connectPorts(producer.getOutputPort(), merger.getNewInputPort());

		merger.declareActive();
	}

	InputPort<T> getInputPort() {
		return merger.getNewInputPort();
	}

	OutputPort<T> getOutputPort() {
		return merger.getOutputPort();
	}
}
