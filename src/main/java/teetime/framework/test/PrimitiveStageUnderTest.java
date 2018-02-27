package teetime.framework.test;

import java.util.List;

import teetime.framework.*;

class PrimitiveStageUnderTest implements StageUnderTest {

	private final AbstractStage stage;

	public PrimitiveStageUnderTest(final AbstractStage stage) {
		this.stage = stage;

		if (stage.getCurrentState() != StageState.CREATED) {
			String message = "This stage has already been tested in this test method. Move this test into a new test method.";
			throw new InvalidTestCaseSetupException(message);
		}

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
