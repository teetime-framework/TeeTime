package teetime.framework;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;

public class RunnableConsumerStageTestConfiguration extends AnalysisConfiguration {

	private final List<Integer> collectedElements = new ArrayList<Integer>();
	private final CollectorSink<Integer> collectorSink;

	public RunnableConsumerStageTestConfiguration(final Integer... inputElements) {
		InitialElementProducer<Integer> producer = new InitialElementProducer<Integer>(inputElements);
		// addThreadableStage(producer);

		CollectorSink<Integer> collectorSink = new CollectorSink<Integer>(collectedElements);
		addThreadableStage(collectorSink);

		IPipeFactory pipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);
		pipeFactory.create(producer.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;
	}

	public List<Integer> getCollectedElements() {
		return collectedElements;
	}

	public Thread getConsumerThread() {
		return collectorSink.getOwningThread();
	}
}
