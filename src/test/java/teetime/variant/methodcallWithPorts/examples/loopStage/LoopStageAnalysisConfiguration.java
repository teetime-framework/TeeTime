package teetime.variant.methodcallWithPorts.examples.loopStage;

import teetime.variant.methodcallWithPorts.framework.core.Configuration;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;

public class LoopStageAnalysisConfiguration extends Configuration {

	public LoopStageAnalysisConfiguration() {
		Countdown countdown = new Countdown(10);

		PipeFactory.INSTANCE.create(ThreadCommunication.INTRA)
				.connectPorts(countdown.getNewCountdownOutputPort(), countdown.getCountdownInputPort());

		this.getFiniteProducerStages().add(countdown);
	}
}
