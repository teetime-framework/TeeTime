package teetime.examples.loopStage;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class LoopStageAnalysisConfiguration extends AnalysisConfiguration {

	public LoopStageAnalysisConfiguration() {
		Countdown countdown = new Countdown(10);

		IPipeFactory factory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.QUEUE_BASED, true);
		factory.create(countdown.getNewCountdownOutputPort(), countdown.getCountdownInputPort());

		this.getFiniteProducerStages().add(countdown);
	}
}
