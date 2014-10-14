package teetime.examples.loopStage;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

import teetime.examples.loopStage.Countdown;

public class LoopStageAnalysisConfiguration extends AnalysisConfiguration {

	private final PipeFactoryRegistry pipeFactory = PipeFactoryRegistry.INSTANCE;

	public LoopStageAnalysisConfiguration() {
		Countdown countdown = new Countdown(10);

		IPipeFactory factory = this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.QUEUE_BASED, true);
		factory.create(countdown.getNewCountdownOutputPort(), countdown.getCountdownInputPort());

		this.getFiniteProducerStages().add(countdown);
	}
}
