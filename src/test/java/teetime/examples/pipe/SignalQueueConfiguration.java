package teetime.examples.pipe;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.ConsumerStage;
import teetime.framework.ProducerStage;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.pipe.SpScPipe;
import teetime.stage.Cache;
import teetime.stage.Clock;

public class SignalQueueConfiguration extends AnalysisConfiguration {

	public SpScPipe pipe;

	public SignalQueueConfiguration() {

		ProducerStage<Long> first = new Clock();
		ConsumerStage<Long> second = new Cache<Long>();

		pipe = (SpScPipe) PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false)
				.create(first.getOutputPort(), second.getInputPort());
	}
}
