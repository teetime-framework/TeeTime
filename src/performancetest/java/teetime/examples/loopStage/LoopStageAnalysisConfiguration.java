package teetime.examples.loopStage;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.pipe.PipeFactory;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;

import teetime.examples.loopStage.Countdown;

public class LoopStageAnalysisConfiguration extends AnalysisConfiguration {

	public LoopStageAnalysisConfiguration() {
		Countdown countdown = new Countdown(10);

		PipeFactory.INSTANCE.create(ThreadCommunication.INTRA)
				.connectPorts(countdown.getNewCountdownOutputPort(), countdown.getCountdownInputPort());

		this.getFiniteProducerStages().add(countdown);
	}
}
