package teetime.examples.loopStage;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactory;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;

public class LoopStageAnalysisConfiguration extends AnalysisConfiguration {

	private final PipeFactory pipeFactory = PipeFactory.INSTANCE;

	public LoopStageAnalysisConfiguration() {
		Countdown countdown = new Countdown(10);

		IPipeFactory factory = this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.QUEUE_BASED, true);
		factory.create(countdown.getNewCountdownOutputPort(), countdown.getCountdownInputPort());

		this.getFiniteProducerStages().add(countdown);
	}
}
