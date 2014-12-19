package teetime.framework;

import teetime.framework.idle.YieldStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.Relay;

class YieldStrategyConfiguration extends AnalysisConfiguration {
	private final IPipeFactory intraThreadPipeFactory;
	private final IPipeFactory interThreadPipeFactory;

	private CollectorSink<Object> collectorSink;

	public YieldStrategyConfiguration(final Object... elements) {
		intraThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		interThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

		InitialElementProducer<Object> producer = buildProducer(elements);
		addThreadableStage(producer);

		Stage consumer = buildConsumer(producer);
		addThreadableStage(consumer);
	}

	private InitialElementProducer<Object> buildProducer(final Object... elements) {
		InitialElementProducer<Object> initialElementProducer = new InitialElementProducer<Object>(elements);

		return initialElementProducer;
	}

	private Relay<Object> buildConsumer(final InitialElementProducer<Object> producer) {
		Relay<Object> relay = new Relay<Object>();
		CollectorSink<Object> collectorSink = new CollectorSink<Object>();

		relay.setIdleStrategy(new YieldStrategy());

		interThreadPipeFactory.create(producer.getOutputPort(), relay.getInputPort());
		intraThreadPipeFactory.create(relay.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;

		return relay;
	}

	public CollectorSink<Object> getCollectorSink() {
		return collectorSink;
	}
}
