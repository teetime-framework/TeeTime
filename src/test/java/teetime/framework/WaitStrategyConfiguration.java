package teetime.framework;

import teetime.framework.idle.WaitStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.Clock;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.Relay;
import teetime.stage.basic.Delay;

class WaitStrategyConfiguration extends AnalysisConfiguration {

	private final IPipeFactory intraThreadPipeFactory;
	private final IPipeFactory interThreadPipeFactory;

	private Delay<Object> delay;
	private CollectorSink<Object> collectorSink;

	public WaitStrategyConfiguration(final long initialDelayInMs, final Object... elements) {
		intraThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		interThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false);

		Stage producer = buildProducer(elements);
		addThreadableStage(producer);

		Stage consumer = buildConsumer(delay);
		addThreadableStage(consumer);

		Clock clock = buildClock(initialDelayInMs, delay);
		addThreadableStage(clock);
	}

	private Clock buildClock(final long initialDelayInMs, final Delay<Object> delay) {
		Clock clock = new Clock();
		clock.setInitialDelayInMs(initialDelayInMs);

		interThreadPipeFactory.create(clock.getOutputPort(), delay.getTimestampTriggerInputPort());

		return clock;
	}

	private Stage buildProducer(final Object... elements) {
		InitialElementProducer<Object> initialElementProducer = new InitialElementProducer<Object>(elements);
		delay = new Delay<Object>();

		intraThreadPipeFactory.create(initialElementProducer.getOutputPort(), delay.getInputPort());

		return initialElementProducer;
	}

	private Relay<Object> buildConsumer(final Delay<Object> delay) {
		Relay<Object> relay = new Relay<Object>();
		CollectorSink<Object> collectorSink = new CollectorSink<Object>();

		relay.setIdleStrategy(new WaitStrategy(relay));

		interThreadPipeFactory.create(delay.getOutputPort(), relay.getInputPort());
		intraThreadPipeFactory.create(relay.getOutputPort(), collectorSink.getInputPort());

		this.collectorSink = collectorSink;

		return relay;
	}

	public CollectorSink<Object> getCollectorSink() {
		return collectorSink;
	}
}
