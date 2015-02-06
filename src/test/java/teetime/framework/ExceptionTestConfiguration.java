package teetime.framework;

import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class ExceptionTestConfiguration extends AnalysisConfiguration {

	public ExceptionTestConfiguration() {
		ExceptionTestProducerStage first = new ExceptionTestProducerStage();
		ExceptionTestConsumerStage second = new ExceptionTestConsumerStage();
		ExceptionTestProducerStage third = new ExceptionTestProducerStage();

		PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTER, PipeOrdering.QUEUE_BASED, false)
				.create(first.getOutputPort(), second.getInputPort(), 4);
		// this.addThreadableStage(new ExceptionTestStage());

		this.addThreadableStage(first);
		this.addThreadableStage(second);
		this.addThreadableStage(third);
	}
}
