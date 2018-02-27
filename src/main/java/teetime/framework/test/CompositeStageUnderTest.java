package teetime.framework.test;

import java.util.List;

import teetime.framework.*;

class CompositeStageUnderTest implements StageUnderTest {

	private final CompositeStage compositeStage;

	public CompositeStageUnderTest(final CompositeStage compositeStage) {
		this.compositeStage = compositeStage;

		List<InputPort<?>> inputPorts = StageFacade.INSTANCE.getInputPorts(compositeStage);
		for (InputPort<?> inputPort : inputPorts) {
			AbstractStage stage = inputPort.getOwningStage();
			if (stage.getCurrentState() != StageState.CREATED) {
				String message = "This stage has already been tested in this test method. Move this test into a new test method.";
				throw new InvalidTestCaseSetupException(message);
			}
		}
	}

	@Override
	public List<InputPort<?>> getInputPorts() {
		return StageFacade.INSTANCE.getInputPorts(compositeStage);
	}

	@Override
	public List<OutputPort<?>> getOutputPorts() {
		return StageFacade.INSTANCE.getOutputPorts(compositeStage);
	}

	@Override
	public void declareActive() {
		for (InputPort<?> inputPort : getInputPorts()) {
			inputPort.getOwningStage().declareActive();
		}
		// do nothing
	}

}
