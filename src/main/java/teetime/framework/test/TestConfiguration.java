package teetime.framework.test;

import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.Configuration;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

class TestConfiguration<I> extends Configuration {

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public TestConfiguration(final List<InputHolder<I>> inputHolders, final AbstractStage stage, final List<OutputHolder<?>> outputHolders) {
		if (inputHolders.isEmpty() && outputHolders.isEmpty()) {
			throw new InvalidTestCaseSetupException("The stage under test must at least receive or send anything.");
		}

		for (InputHolder<I> inputHolder : inputHolders) {
			final InitialElementProducer<I> producer = new InitialElementProducer<I>(inputHolder.getInputElements());
			connectPorts(producer.getOutputPort(), inputHolder.getPort());
		}

		stage.declareActive();

		for (OutputHolder<?> outputHolder : outputHolders) {
			final CollectorSink<Object> sink = new CollectorSink<Object>(outputHolder.getOutputElements());
			connectPorts(outputHolder.getPort(), sink.getInputPort());
		}
	}
}
