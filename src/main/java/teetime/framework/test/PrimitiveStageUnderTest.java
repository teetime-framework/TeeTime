package teetime.framework.test;

import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.StageFacade;

class PrimitiveStageUnderTest implements StageUnderTest {

	private final AbstractStage stage;

	public PrimitiveStageUnderTest(final AbstractStage stage) {
		this.stage = stage;
	}

	@Override
	public List<InputPort<?>> getInputPorts() {
		return StageFacade.INSTANCE.getInputPorts(stage);
	}

	@Override
	public List<OutputPort<?>> getOutputPorts() {
		return StageFacade.INSTANCE.getOutputPorts(stage);
	}

	@Override
	public void declareActive() {
		stage.declareActive();
	}

}
